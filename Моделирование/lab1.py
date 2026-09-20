import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy import stats

df = pd.read_csv("/home/lena/Desktop/моделирование_1.csv", header=None)

x = df[0].to_numpy()[:300]
kol = [10, 20, 50, 100, 200, 300]
dov_ver = [0.9, 0.95, 0.99]

etalon = 300
etalon_data = x[:etalon]

etalon_mean = np.mean(etalon_data)
etalon_var = np.var(etalon_data, ddof=1)
etalon_sco = np.std(etalon_data, ddof=1)
etalon_k_var = etalon_sco / etalon_mean

etalon_margins = []
for dov in dov_ver:
    alpha = 1 - dov
    t_critical = stats.t.ppf(1 - alpha / 2, df=etalon - 1)
    etalon_margins.append(t_critical * etalon_sco / np.sqrt(etalon))


for k in kol:
    data = x[:k]

    x_mean = np.mean(data)
    x_var = np.var(data, ddof=1)
    x_sco = np.std(data, ddof=1)
    x_k_var = x_sco / x_mean

    print("kol = ", k, end="")
    print(f", M = {x_mean:.4f}", end="")
    print(f", D = {x_var:.4f}", end="")
    print(f", SCO = {x_sco:.4f}", end="")
    print(f", K_VAR = {x_k_var:.4f}")

    print(
        f"M отклонение = {(np.abs(x_mean - etalon_mean) / etalon_mean * 100):.2f}",
        end="",
    )
    print(
        f", D отклонение = {(np.abs(x_var - etalon_var) / etalon_var * 100):.2f}",
        end="",
    )
    print(
        f", SCO отклонение = {(np.abs(x_sco - etalon_sco) / etalon_sco * 100):.2f}",
        end="",
    )
    print(
        f", K_VAR отклонение = {(np.abs(x_k_var - etalon_k_var) / etalon_k_var * 100):.2f}"
    )
    for i in range(3):
        dov = dov_ver[i]
        alpha = 1 - dov
        t_critical = stats.t.ppf(1 - alpha / 2, df=k - 1)
        margin_of_error = t_critical * x_sco / np.sqrt(k)
        print(f"dov = {dov}, margin_of_error = {margin_of_error:.4f}")
        print(
            f"dov = {dov}, margin_of_error отклонение = {(np.abs(margin_of_error - etalon_margins[i]) / etalon_margins[i] * 100):.2f}"
        )
    print("------------------------------------------------------")

# график

plt.plot(range(1, len(x) + 1), x)

plt.xlabel("Номер измерения")
plt.ylabel("Значение")
plt.title("График заданной числовой последовательности")
plt.grid()
plt.show()

# Автокорреляционный анализ

n = len(x)
mean = np.mean(x)

autocorr = []

for k in range(1, 11):
    numerator = np.sum((x[: n - k] - mean) * (x[k:] - mean))

    denominator = np.sum((x - mean) ** 2)

    r = numerator / denominator
    autocorr.append(r)

    print(f"Сдвиг k = {k}, автокорреляция = {r:.4f}")

# график автокоррел.
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
