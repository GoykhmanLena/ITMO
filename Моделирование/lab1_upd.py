import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy import stats


def calculate_statistics(data):
    n = len(data)

    x_mean = np.mean(data)
    x_var = np.var(data, ddof=1)
    x_sco = np.std(data, ddof=1)
    x_k_var = x_sco / x_mean

    margins = []

    for dov in dov_ver:
        alpha = 1 - dov
        t_critical = stats.t.ppf(1 - alpha / 2, df=n - 1)
        margin_of_error = t_critical * x_sco / np.sqrt(n)
        margins.append(margin_of_error)

    return x_mean, x_var, x_sco, x_k_var, margins


def calculate_autocorrelation(data):
    n = len(data)
    mean = np.mean(data)

    autocorr = []

    for k in range(1, 11):
        numerator = np.sum((data[: n - k] - mean) * (data[k:] - mean))

        denominator = np.sum((data - mean) ** 2)

        r = numerator / denominator
        autocorr.append(r)

    return autocorr


def generate_erlang(n, k_erlang, alpha):
    data = np.zeros(n)

    for i in range(k_erlang):
        data += np.random.exponential(scale=1 / alpha, size=n)

    return data


def calculate_percent_ratio(generated_value, given_value):
    return np.abs(generated_value - given_value) / np.abs(given_value) * 100


df = pd.read_csv("/home/lena/Desktop/моделирование_1.csv", header=None)

x = df[0].to_numpy()[:300]

kol = [10, 20, 50, 100, 200, 300]
dov_ver = [0.9, 0.95, 0.99]

etalon = 300
etalon_data = x[:etalon]

etalon_mean, etalon_var, etalon_sco, etalon_k_var, etalon_margins = (
    calculate_statistics(etalon_data)
)

given_statistics = {}

for k in kol:
    data = x[:k]

    x_mean, x_var, x_sco, x_k_var, margins = calculate_statistics(data)

    given_statistics[k] = {
        "mean": x_mean,
        "var": x_var,
        "sco": x_sco,
        "k_var": x_k_var,
        "margins": margins,
    }

    print("kol = ", k, end="")
    print(f", M = {x_mean:.2f}", end="")
    print(f", D = {x_var:.2f}", end="")
    print(f", SCO = {x_sco:.2f}", end="")
    print(f", K_VAR = {x_k_var:.2f}")

    print(
        f"M отклонение = {np.abs(x_mean - etalon_mean) / etalon_mean * 100:.2f}",
        end="",
    )

    print(
        f", D отклонение = {np.abs(x_var - etalon_var) / etalon_var * 100:.2f}",
        end="",
    )

    print(
        f", SCO отклонение = {np.abs(x_sco - etalon_sco) / etalon_sco * 100:.2f}",
        end="",
    )

    print(
        f", K_VAR отклонение = {np.abs(x_k_var - etalon_k_var) / etalon_k_var * 100:.2f}"
    )

    for i in range(3):
        dov = dov_ver[i]
        margin_of_error = margins[i]

        print(f"dov = {dov}, margin_of_error = {margin_of_error:.2f}")

        print(
            f"dov = {dov}, margin_of_error отклонение = {np.abs(margin_of_error - etalon_margins[i]) / etalon_margins[i] * 100:.2f}"
        )

    print("------------------------------------------------------\n")

# график
plt.plot(range(1, len(x) + 1), x)
plt.xlabel("Номер измерения")
plt.ylabel("Значение")
plt.title("График заданной числовой последовательности")
plt.grid()
plt.show()

# Автокорреляционный анализ
autocorr = calculate_autocorrelation(x)

print("\nАвтокорреляция:")

for k in range(1, 11):
    print(f"k = {k}, r = {autocorr[k - 1]:.2f}")

# график корреляции
plt.plot(range(1, 11), autocorr, marker="o")
plt.axhline(0, linewidth=1)
plt.xlabel("Сдвиг k")
plt.ylabel("Коэффициент автокорреляции")
plt.title("Автокорреляционная функция заданной последовательности")
plt.grid()
plt.show()

# гистограмма
plt.hist(x, bins=20)
plt.xlabel("Значение")
plt.ylabel("Частота")
plt.title("Гистограмма распределения частот")
plt.grid()
plt.show()


# генератор
k_erlang = 3

param_alpha = k_erlang / etalon_mean

print("\nПараметры распределения Эрланга:")
print(f"k = {k_erlang}")
print(f"lambda = {param_alpha:.6f}")

# generated_nums = generate_erlang(300, k_erlang, param_alpha)
gen = pd.read_csv("/home/lena/Desktop/моделирование_1_generated.csv", header=None)

generated_nums = gen[0].to_numpy()[:300]

print("\nХарактеристики сгенерированной последовательности:")

for k in kol:
    generated_data = generated_nums[:k]

    (
        generated_mean,
        generated_var,
        generated_sco,
        generated_k_var,
        generated_margins,
    ) = calculate_statistics(generated_data)

    # Берём уже сохранённые характеристики заданной ЧП
    x_mean = given_statistics[k]["mean"]
    x_var = given_statistics[k]["var"]
    x_sco = given_statistics[k]["sco"]
    x_k_var = given_statistics[k]["k_var"]
    margins = given_statistics[k]["margins"]

    print("\n---------------------------------------------------------------")
    print(f"Количество элементов = {k}")

    print("\nЗаданная ЧП:")
    print(f"M = {x_mean:.2f}")
    print(f"D = {x_var:.2f}")
    print(f"SCO = {x_sco:.2f}")
    print(f"K_VAR = {x_k_var:.2f}")

    print("\nСгенерированная ЧП:")
    print(f"M = {generated_mean:.2f}")
    print(f"D = {generated_var:.2f}")
    print(f"SCO = {generated_sco:.2f}")
    print(f"K_VAR = {generated_k_var:.2f}")

    print("\nОтносительное отклонение:")
    print(f"M = {calculate_percent_ratio(generated_mean, x_mean):.2f}%")

    print(f"D = {calculate_percent_ratio(generated_var, x_var):.2f}%")

    print(f"SCO = {calculate_percent_ratio(generated_sco, x_sco):.2f}%")

    print(f"K_VAR = {calculate_percent_ratio(generated_k_var, x_k_var):.2f}%")

    print("\nДоверительные интервалы:")

    for i in range(3):
        dov = dov_ver[i]

        print(
            f"dov = {dov}: "
            f"заданная = ±{margins[i]:.2f}, "
            f"сгенерированная = ±{generated_margins[i]:.2f}"
        )

        print(
            f"  отклонение = {calculate_percent_ratio(generated_margins[i], margins[i]):.2f}%"
        )


# график 2
plt.plot(range(1, len(x) + 1), generated_nums)
plt.xlabel("Номер измерения")
plt.ylabel("Значение")
plt.title("График сгенерированной числовой последовательности")
plt.grid()
plt.show()


generated_autocorr = calculate_autocorrelation(generated_nums)

print("\nАвтокорреляция сгенерированной последовательности:")

for k in range(1, 11):
    print(
        f"k = {k}, r = {generated_autocorr[k - 1]:.2f}, откл = {calculate_percent_ratio(generated_autocorr[k - 1], autocorr[k - 1]):.2f}"
    )


plt.plot(range(1, 11), generated_autocorr, marker="o")

plt.axhline(0, linewidth=1)

plt.xlabel("Сдвиг k")
plt.ylabel("Коэффициент автокорреляции")
plt.title("Автокорреляционная функция сгенерированной последовательности")
plt.grid()
plt.show()

# гистограмма 2
plt.hist(generated_nums, bins=20)
plt.xlabel("Значение")
plt.ylabel("Частота")
plt.title("Гистограмма распределения частот сгенерированной последовательности")
plt.grid()
plt.show()


correlation = np.corrcoef(x, generated_nums)[0, 1]

print("\nКоэффициент корреляции заданной и сгенерированной ЧП:")
print(f"r = {correlation:.2f}")
