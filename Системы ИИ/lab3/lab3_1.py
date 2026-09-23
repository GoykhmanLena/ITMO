import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


def solve_gauss(A, b):
    A = A.astype(float).copy()
    b = b.astype(float).copy()
    n = A.shape[0]

    for k in range(n):
        pivot_row = k + np.argmax(np.abs(A[k:, k]))
        if abs(A[pivot_row, k]) < 1e-12:
            raise ValueError("Матрица вырождена")
        if pivot_row != k:
            A[[k, pivot_row]] = A[[pivot_row, k]]
            b[[k, pivot_row]] = b[[pivot_row, k]]

        for i in range(k + 1, n):
            factor = A[i, k] / A[k, k]
            A[i, k:] -= factor * A[k, k:]
            b[i] -= factor * b[k]

    x = np.zeros(n)
    for i in range(n - 1, -1, -1):
        x[i] = (b[i] - A[i, i + 1 :] @ x[i + 1 :]) / A[i, i]
    return x


def linear_regression_fit(X, y):
    XTX = X.T @ X
    XTy = X.T @ y
    theta = solve_gauss(XTX, XTy)
    return theta


def linear_regression_predict(X, theta):
    return X @ theta


def r_squared(y_true, y_pred):
    ss_res = np.sum((y_true - y_pred) ** 2)
    ss_tot = np.sum((y_true - np.mean(y_true)) ** 2)
    return 1 - ss_res / ss_tot


def add_bias(X):
    return np.hstack([np.ones((X.shape[0], 1)), X])


def normalize(train, test):
    mean = train.mean(axis=0)
    std = train.std(axis=0)
    return (train - mean) / std, (test - mean) / std


def train_test_split_custom(X, y, test_size=0.2, random_state=42):
    rng = np.random.default_rng(random_state)
    n = X.shape[0]
    indices = rng.permutation(n)
    split = int(n * (1 - test_size))
    train_idx, test_idx = indices[:split], indices[split:]
    return X[train_idx], X[test_idx], y[train_idx], y[test_idx]


df = pd.read_csv("/home/lena/Desktop/ITMO/Системы ИИ/lab3/Student_Performance.csv")

print("Первые 5 строк")
print("-" * 100)
print(df.head(10))

print("\n" + "-" * 70)
print("Информация о датасете")
print("-" * 70)

print(df.info())

# статистика
print("\n" + "-" * 70)
print("Статистика")
print("-" * 70)
stats = df.describe(include="all").T
stats["median"] = df.median(numeric_only=True)
stats["quantile_25"] = df.quantile(0.25, numeric_only=True)
stats["quantile_75"] = df.quantile(0.75, numeric_only=True)
print(stats)

# графики
fig, axes = plt.subplots(2, 3, figsize=(18, 10))
numeric_cols = df.select_dtypes(include=[np.number]).columns

for ax, col in zip(axes.flatten(), numeric_cols):
    ax.hist(df[col], bins=20, color="steelblue", edgecolor="black", alpha=0.7)
    ax.set_title(f"Распределение: {col}")
    ax.set_xlabel(col)
    ax.set_ylabel("Частота")


for i in range(len(numeric_cols), 6):
    axes.flatten()[i].axis("off")

plt.tight_layout()
plt.savefig("statistics_visualization.png", dpi=100)
plt.show()

# Boxplot для наглядности
plt.figure(figsize=(14, 6))
df[numeric_cols].boxplot()
plt.title("Boxplot числовых признаков")
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig("boxplot.png", dpi=100)
plt.show()

# Предобработка
print("\n" + "-" * 60)
print("Предобработка")
print("-" * 60)

# Проверка пропущенных значений
print("Пропущенные значения по колонкам:")
print(df.isnull().sum())


# Кодирование Yes/No -> 1/0
df["Extracurricular Activities"] = df["Extracurricular Activities"].map(
    {"Yes": 1, "No": 0}
)

print("\nПосле кодирования:")
print(df.head())


target = "Performance Index"
feature_cols = [c for c in df.columns if c != target]

X_all = df[feature_cols].values.astype(float)
y_all = df[target].values.astype(float)


# df["Synthetic_HS_x_PS"] = df["Hours Studied"] * df["Previous Scores"]
# df["Synthetic_HS_x_PS"] = df["Hours Studied"] / df["Sleep Hours"]
df["Synthetic_HS_x_PS"] = df["Hours Studied"] * df["Extracurricular Activities"]
models_config = {
    "Модель 1:": ["Hours Studied"],
    "Модель 2:": ["Hours Studied", "Previous Scores"],
    "Модель 3:": [
        "Hours Studied",
        "Previous Scores",
        "Extracurricular Activities",
        "Sleep Hours",
        "Sample Question Papers Practiced",
    ],
    "Модель 4 (бонус):": [
        "Hours Studied",
        "Previous Scores",
        "Extracurricular Activities",
        "Sleep Hours",
        "Sample Question Papers Practiced",
        "Synthetic_HS_x_PS",
    ],
}

results = {}

for name, cols in models_config.items():
    print("\n" + "-" * 60)
    print(name)
    print("-" * 60)

    X = df[cols].values.astype(float)
    y = df[target].values.astype(float)

    X_train, X_test, y_train, y_test = train_test_split_custom(
        X, y, test_size=0.2, random_state=42
    )

    X_train_n, X_test_n = normalize(X_train, X_test)
    X_train_b = add_bias(X_train_n)
    X_test_b = add_bias(X_test_n)

    theta = linear_regression_fit(X_train_b, y_train)

    y_train_pred = linear_regression_predict(X_train_b, theta)
    y_test_pred = linear_regression_predict(X_test_b, theta)

    r2_train = r_squared(y_train, y_train_pred)
    r2_test = r_squared(y_test, y_test_pred)

    results[name] = {
        "theta": theta,
        "r2_train": r2_train,
        "r2_test": r2_test,
        "y_test": y_test,
        "y_test_pred": y_test_pred,
        "features": cols,
    }

    print(f"Признаки: {cols}")
    print(f"Коэффициенты: {np.round(theta, 4)}")
    print(f"R² train: {r2_train:.4f}")
    print(f"R² test : {r2_test:.4f}")

# сравнение
print("\n" + "-" * 60)
print("Сравнение")
print("-" * 60)
comparison = pd.DataFrame(
    {
        "Модель": list(results.keys()),
        "R² (train)": [results[k]["r2_train"] for k in results],
        "R² (test)": [results[k]["r2_test"] for k in results],
        "Кол-во признаков": [len(results[k]["features"]) for k in results],
    }
)
print(comparison.to_string(index=False))


# сравнение с синтетическим
print("\n" + "-" * 60)
print("Модель 3 vs Модель 4")
print("-" * 60)
r2_m3 = results["Модель 3:"]["r2_test"]
r2_m4 = results["Модель 4 (бонус):"]["r2_test"]
print(f"R² (test) Модель 3 (без синтетики): {r2_m3:.4f}")
print(f"R² (test) Модель 4 (с синтетикой):  {r2_m4:.4f}")
print(f"Прирост R²:                          {r2_m4 - r2_m3:+.4f}")

# лучшая модель
best_name = max(results, key=lambda k: results[k]["r2_test"])
best = results[best_name]
print(f"\nЛучшая модель: {best_name}")
print(f"R² test: {best['r2_test']:.4f}")

plt.figure(figsize=(8, 8))
plt.scatter(best["y_test"], best["y_test_pred"], alpha=0.4, s=10, color="steelblue")
lims = [
    min(best["y_test"].min(), best["y_test_pred"].min()),
    max(best["y_test"].max(), best["y_test_pred"].max()),
]
plt.plot(lims, lims, "r--", label="Точное совпадение")
plt.xlabel("Фактическое значение")
plt.ylabel("Предсказанное значение")
plt.title(f"Факт vs Прогноз — {best_name}")
plt.legend()
plt.grid(alpha=0.3)
plt.tight_layout()
plt.savefig("best_model.png", dpi=100)
plt.show()
