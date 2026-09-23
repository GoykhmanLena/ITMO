import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

file = "/home/lena/Desktop/ITMO/Системы ИИ/lab3/Student_Performance.csv"

df = pd.read_csv(file)

print("Первые 5 строк")
print("-" * 100)
print(df.head(10))

print("\n" + "-" * 70)
print("Информация о датасете")
print("-" * 70)
print(f"Количество строк: {df.shape[0]}")
print(f"Количество столбцов: {df.shape[1]}")

print(df.info())


# Пропуски
print("\n" + "-" * 70)
print("Пропуски")
print("-" * 70)

missing = df.isnull().sum()
print(missing)

print("\nКоличество пропущенных значений:", df.isnull().sum().sum())


# Статистика
print("\n" + "-" * 70)
print("Статистика")
print("-" * 70)

# count, mean, std, min, 25%, 50%, 75%, max
statistics = df.describe(include="all").T

print(statistics)


# Квантили

print("\n" + "-" * 70)
print("Квантили")
print("-" * 70)

numeric_columns = df.select_dtypes(include=np.number).columns
quantiles = df[numeric_columns].quantile([0.10, 0.25, 0.50, 0.75, 0.90])

print(quantiles)


# Гистограммы

df[numeric_columns].hist(figsize=(14, 10), bins=20)

plt.suptitle("Распределение числовых признаков", fontsize=16)

plt.tight_layout()
plt.show()


# Boxplot

plt.figure(figsize=(14, 7))

df[numeric_columns].boxplot()

plt.title("Boxplot числовых признаков")
plt.xticks(rotation=45)
plt.ylabel("Значение")
plt.grid(True)

plt.tight_layout()
plt.show()


TARGET = "Performance Index"

# Предобработка

data = df.copy()

duplicates = data.duplicated().sum()

print("\n" + "=" * 70)
print("Дубликаты")
print("-" * 70)
print("Количество дубликатов:", duplicates)

data = data.drop_duplicates()


# Обработка пропущенных значений
numeric_cols = data.select_dtypes(include=np.number).columns
categorical_cols = data.select_dtypes(exclude=np.number).columns

# Числовые признаки заменяем медианой
for col in numeric_cols:
    if data[col].isnull().sum() > 0:
        data[col] = data[col].fillna(data[col].median())

# Категориальные признаки заменяем модой
for col in categorical_cols:
    if data[col].isnull().sum() > 0:
        data[col] = data[col].fillna(data[col].mode()[0])


# ============================================================
# 8. РАЗДЕЛЕНИЕ НА X И y
# ============================================================

X = data.drop(columns=[TARGET])
y = data[TARGET].astype(float).values


# ============================================================
# 9. КОДИРОВАНИЕ КАТЕГОРИАЛЬНЫХ ПРИЗНАКОВ
# ============================================================

# One-hot encoding через Pandas.
#
# drop_first=True:
# для категориального признака с k категориями
# создаётся k-1 бинарных столбцов.
#
# Это предотвращает идеальную линейную зависимость
# между dummy-переменными.

X = pd.get_dummies(X, drop_first=True, dtype=float)

print("\n" + "=" * 70)
print("ПРИЗНАКИ ПОСЛЕ ONE-HOT ENCODING")
print("=" * 70)

print(X.columns.tolist())


# ============================================================
# 10. ПРЕОБРАЗУЕМ X В NUMPY
# ============================================================

X = X.astype(float).values


# ============================================================
# 11. TRAIN / TEST SPLIT
# ============================================================

# Не используем sklearn.
# Разбиение выполняем самостоятельно.

np.random.seed(42)

n = len(X)

indices = np.arange(n)

np.random.shuffle(indices)

train_size = int(0.8 * n)

train_indices = indices[:train_size]
test_indices = indices[train_size:]

X_train = X[train_indices]
X_test = X[test_indices]

y_train = y[train_indices]
y_test = y[test_indices]

print("\n" + "=" * 70)
print("РАЗДЕЛЕНИЕ ДАННЫХ")
print("=" * 70)

print("Размер X_train:", X_train.shape)
print("Размер X_test :", X_test.shape)
print("Размер y_train:", y_train.shape)
print("Размер y_test :", y_test.shape)


# ============================================================
# 12. НОРМИРОВКА ПРИЗНАКОВ
# ============================================================

# Используем Z-score normalization:
#
# x_norm = (x - mean) / std
#
# Очень важно:
# mean и std вычисляются ТОЛЬКО по train.
# Иначе информация из test попадёт в обучение.

mean_train = X_train.mean(axis=0)
std_train = X_train.std(axis=0)

# Защита от деления на 0
std_train[std_train == 0] = 1

X_train_norm = (X_train - mean_train) / std_train
X_test_norm = (X_test - mean_train) / std_train


# ============================================================
# 13. СОБСТВЕННАЯ ЛИНЕЙНАЯ РЕГРЕССИЯ
# ============================================================


class LinearRegressionOLS:
    def __init__(self):
        self.weights = None

    def fit(self, X, y):
        """
        Обучение модели методом наименьших квадратов.

        Ищем коэффициенты beta:

            beta = argmin ||y - X beta||^2

        Используем уравнение:

            beta = (X^T X)^(-1) X^T y

        Вместо np.linalg.solve/lstsq
        используем собственную реализацию
        обращения матрицы методом Гаусса-Жордана.
        """

        # Добавляем столбец единиц для свободного члена
        X_bias = np.column_stack([np.ones(X.shape[0]), X])

        A = X_bias.T @ X_bias
        b = X_bias.T @ y

        # Решаем A * beta = b
        self.weights = self._gauss_jordan(A, b)

        return self

    def predict(self, X):
        """
        Предсказание:
            y_pred = X beta
        """

        X_bias = np.column_stack([np.ones(X.shape[0]), X])

        return X_bias @ self.weights

    @staticmethod
    def _gauss_jordan(A, b):
        """
        Решение системы A*x=b
        методом Гаусса-Жордана.

        Здесь НЕ используются:
            np.linalg.inv
            np.linalg.solve
            np.linalg.lstsq
        """

        A = A.astype(float).copy()
        b = np.asarray(b, dtype=float).reshape(-1, 1)

        n = A.shape[0]

        # Расширенная матрица
        augmented = np.hstack([A, b])

        for i in range(n):
            # Ищем строку с максимальным элементом
            # в текущем столбце.
            pivot_row = i + np.argmax(np.abs(augmented[i:, i]))

            # Проверка на вырожденность
            if abs(augmented[pivot_row, i]) < 1e-12:
                raise ValueError("Матрица вырождена или признаки линейно зависимы.")

            # Меняем строки местами
            if pivot_row != i:
                augmented[[i, pivot_row]] = augmented[[pivot_row, i]]

            # Нормируем ведущий элемент
            pivot = augmented[i, i]

            augmented[i] = augmented[i] / pivot

            # Обнуляем остальные элементы
            for j in range(n):
                if j != i:
                    factor = augmented[j, i]

                    augmented[j] = augmented[j] - factor * augmented[i]

        return augmented[:, -1]


# ============================================================
# 14. МЕТРИКА R²
# ============================================================


def r2_score(y_true, y_pred):
    """
    Коэффициент детерминации:

        R² = 1 - SSE / SST

    SSE = сумма квадратов ошибок
    SST = общая сумма квадратов
    """

    ss_res = np.sum((y_true - y_pred) ** 2)

    ss_tot = np.sum((y_true - np.mean(y_true)) ** 2)

    return 1 - ss_res / ss_tot


# ============================================================
# 15. MSE
# ============================================================


def mean_squared_error(y_true, y_pred):

    return np.mean((y_true - y_pred) ** 2)


# ============================================================
# 16. ФУНКЦИЯ ОБУЧЕНИЯ МОДЕЛИ
# ============================================================


def train_model(X_train_model, X_test_model, y_train, y_test, model_name):

    model = LinearRegressionOLS()

    model.fit(X_train_model, y_train)

    train_pred = model.predict(X_train_model)

    test_pred = model.predict(X_test_model)

    train_r2 = r2_score(y_train, train_pred)

    test_r2 = r2_score(y_test, test_pred)

    train_mse = mean_squared_error(y_train, train_pred)

    test_mse = mean_squared_error(y_test, test_pred)

    print("\n" + "=" * 70)
    print(model_name)
    print("=" * 70)

    print(f"R² train: {train_r2:.6f}")
    print(f"R² test : {test_r2:.6f}")

    print(f"MSE train: {train_mse:.6f}")
    print(f"MSE test : {test_mse:.6f}")

    return {
        "name": model_name,
        "model": model,
        "train_pred": train_pred,
        "test_pred": test_pred,
        "train_r2": train_r2,
        "test_r2": test_r2,
        "train_mse": train_mse,
        "test_mse": test_mse,
    }


# ============================================================
# 17. СОЗДАЁМ ТРИ НАБОРА ПРИЗНАКОВ
# ============================================================

# Получаем имена исходных признаков ДО кодирования

original_columns = data.drop(columns=[TARGET]).columns.tolist()

print("\nИсходные признаки:")
print(original_columns)


# ------------------------------------------------------------
# Определяем названия признаков датасета
# ------------------------------------------------------------

# В датасете Kaggle обычно присутствуют:
#
# Hours Studied
# Previous Scores
# Extracurricular Activities
# Sleep Hours
# Sample Question Papers Practiced
#
# Целевая переменная:
#
# Performance Index


# ------------------------------------------------------------
# Модель 1
# Только Hours Studied
# ------------------------------------------------------------

model1_features = ["Hours Studied"]


# ------------------------------------------------------------
# Модель 2
# Hours Studied + Previous Scores + Sleep Hours
# ------------------------------------------------------------

model2_features = ["Hours Studied", "Previous Scores", "Sleep Hours"]


# ------------------------------------------------------------
# Модель 3
# Все исходные признаки
# ------------------------------------------------------------

model3_features = [col for col in original_columns]


# ============================================================
# 18. ФУНКЦИЯ ПОЛУЧЕНИЯ НОРМИРОВАННЫХ ПРИЗНАКОВ
# ============================================================

# Нам нужно знать, какие столбцы X соответствуют
# исходным признакам.
#
# Поэтому сохраняем DataFrame до преобразования в NumPy.

X_df = data.drop(columns=[TARGET])

X_df = pd.get_dummies(X_df, drop_first=True, dtype=float)


# ============================================================
# ФУНКЦИЯ СОЗДАНИЯ ПОДМНОЖЕСТВА ПРИЗНАКОВ
# ============================================================


def get_feature_indices(feature_names, all_columns):

    indices = []

    for feature in feature_names:
        # Если это исходный числовой признак
        if feature in all_columns:
            indices.append(all_columns.index(feature))

        # Если категориальный признак после encoding
        else:
            for i, col in enumerate(all_columns):
                if col.startswith(feature + "_"):
                    indices.append(i)

    return indices


all_encoded_columns = X_df.columns.tolist()


# X
X_all = X_df.values.astype(float)


def prepare_features(feature_names):

    indices = get_feature_indices(feature_names, all_encoded_columns)
    X_selected = X_all[:, indices]
    X_train_selected = X_selected[train_indices]
    X_test_selected = X_selected[test_indices]

    mean = X_train_selected.mean(axis=0)
    std = X_train_selected.std(axis=0)

    std[std == 0] = 1

    X_train_selected = (X_train_selected - mean) / std
    X_test_selected = (X_test_selected - mean) / std
    return (X_train_selected, X_test_selected, indices)


# 1 модель
X1_train, X1_test, indices1 = prepare_features(model1_features)
result1 = train_model(X1_train, X1_test, y_train, y_test, "Модель 1: Hours Studied")

# 2 модель
X2_train, X2_test, indices2 = prepare_features(model2_features)

result2 = train_model(
    X2_train,
    X2_test,
    y_train,
    y_test,
    "Модель 2: Hours Studied + Previous Scores + Sleep Hours",
)

# 3 модель
X3_train, X3_test, indices3 = prepare_features(model3_features)
result3 = train_model(X3_train, X3_test, y_train, y_test, "Модель 3: Все признаки")


# Бонус
# Study_Sleep_Index = Hours Studied * Sleep Hours

data_bonus = data.copy()

data_bonus["Study_Sleep_Index"] = (
    data_bonus["Hours Studied"] * data_bonus["Sleep Hours"]
)

# Формируем X и y

X_bonus_df = data_bonus.drop(columns=[TARGET])
X_bonus_df = pd.get_dummies(X_bonus_df, drop_first=True, dtype=float)
X_bonus_all = X_bonus_df.values.astype(float)
bonus_columns = X_bonus_df.columns.tolist()


# Train/test

X_bonus_train = X_bonus_all[train_indices]
X_bonus_test = X_bonus_all[test_indices]

# Нормировка

bonus_mean = X_bonus_train.mean(axis=0)
bonus_std = X_bonus_train.std(axis=0)
bonus_std[bonus_std == 0] = 1
X_bonus_train = (X_bonus_train - bonus_mean) / bonus_std
X_bonus_test = (X_bonus_test - bonus_mean) / bonus_std


# Бонус

bonus_result = train_model(
    X_bonus_train,
    X_bonus_test,
    y_train,
    y_test,
    "Бонус: Все признаки + Study_Sleep_Index",
)


# Сравнение моделей

results = [result1, result2, result3, bonus_result]

comparison = pd.DataFrame(
    {
        "Модель": [r["name"] for r in results],
        "R² train": [r["train_r2"] for r in results],
        "R² test": [r["test_r2"] for r in results],
        "MSE train": [r["train_mse"] for r in results],
        "MSE test": [r["test_mse"] for r in results],
    }
)


print("\n" + "=" * 70)
print("СРАВНЕНИЕ МОДЕЛЕЙ")
print("=" * 70)

print(comparison.to_string(index=False))

# R^2

plt.figure(figsize=(12, 6))
x = np.arange(len(comparison))
width = 0.35
plt.bar(x - width / 2, comparison["R² train"], width, label="Train R²")
plt.bar(x + width / 2, comparison["R² test"], width, label="Test R²")
plt.xticks(x, comparison["Модель"], rotation=20, ha="right")
plt.ylabel("R²")
plt.title("Сравнение моделей по коэффициенту детерминации")
plt.legend()
plt.grid(axis="y")

plt.tight_layout()
plt.show()


for result in results:
    plt.figure(figsize=(7, 6))
    plt.scatter(y_test, result["test_pred"], alpha=0.6)
    min_value = min(y_test.min(), result["test_pred"].min())
    max_value = max(y_test.max(), result["test_pred"].max())
    plt.plot([min_value, max_value], [min_value, max_value])
    plt.xlabel("Фактическое значение")
    plt.ylabel("Предсказанное значение")
    plt.title(f"{result['name']}\nTest R² = {result['test_r2']:.4f}")
    plt.grid(True)
    plt.tight_layout()
    plt.show()


# Коэффициенты

print("\n" + "-" * 70)
print("Коэффициенты")
print("-" * 70)


def print_coefficients(result, feature_names):

    model = result["model"]
    print("\n" + result["name"])
    print(f"Свободный член: {model.weights[0]:.6f}")

    for i, feature in enumerate(feature_names):
        print(f"{feature}: {model.weights[i + 1]:.6f}")


print_coefficients(result1, model1_features)
print_coefficients(result2, model2_features)
print_coefficients(result3, model3_features)

print("\n" + "-" * 70)
print("Выводы")
print("-" * 70)

for result in results:
    print(f"\n{result['name']}")
    print(f"  R² на train: {result['train_r2']:.4f}")
    print(f"  R² на test : {result['test_r2']:.4f}")
    print(f"  MSE на test: {result['test_mse']:.4f}")
