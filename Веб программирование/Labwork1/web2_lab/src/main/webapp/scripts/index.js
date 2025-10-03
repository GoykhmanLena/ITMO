const VALID_XS = new Set([-4, -3, -2, -1, 0, 1, 2, 3, 4]);
const VALID_RS = new Set([1.0, 1.5, 2.0, 2.5, 3.0]); // Используем double для R

function submitForm(ev) {
    ev.preventDefault(); // Предотвращаем стандартную отправку формы

    const errorDiv = document.getElementById("error");
    if (errorDiv) {
        errorDiv.hidden = true; // Скрываем ошибки перед новой попыткой
        errorDiv.innerText = "";
    }

    const form = ev.target;
    const formData = new FormData(form);
    const values = Object.fromEntries(formData);

    try {
       // validateInput(values); // Вызываем валидацию перед отправкой
        form.submit(); // Если валидация прошла, отправляем форму
    } catch (e) {
        if (errorDiv) {
            errorDiv.hidden = false;
            errorDiv.innerText = e.message;
        }
    }
}

function validateInput(values) {
    // Валидация X: теперь значение приходит из скрытого поля
    if (values.x === undefined || values.x === "") {
        throw new Error("X is required. Please select an X value.");
    }
    const x = Number(values.x);
    if (isNaN(x) || !VALID_XS.has(x)) {
        throw new Error(`X must be one of [${[...VALID_XS].join(", ")}].`);
    }

    // Валидация Y
    if (values.y === undefined || values.y === "") {
        throw new Error("Y is required.");
    }
    const y = Number(values.y);
    if (isNaN(y) || y < -3 || y > 5) { // Диапазон -3 до 5, как в задании и AreaCheckServlet
        throw new Error("Y must be in [-3, 5].");
    }

    // Валидация R
    if (values.r === undefined) {
        throw new Error("R is required. Please select an R value.");
    }
    const r = Number(values.r);
    if (isNaN(r) || !VALID_RS.has(r)) {
        throw new Error(`R must be one of [${[...VALID_RS].join(", ")}].`);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // ... (ваш существующий код DOMContentLoaded) ...

    const isResultPage = window.location.pathname.includes("/controller"); // Используем /controller

    if (isResultPage) {
        // На resultPage.jsp вызываем функцию обновления изображения
        updateResultImage();

        // Также, если на resultPage.jsp, то скорее всего data-form не нужен
        // Можно убрать этот блок, если форма не должна работать на resultPage
        const form = document.getElementById("data-form");
        if (form) {
            form.removeEventListener("submit", submitForm); // Убираем слушатель, если он был добавлен
            form.hidden = true; // Скрываем форму, если она есть на этой странице
        }
    } else {
        // Это index.jsp
        const form = document.getElementById("data-form");
        if (form) {
            form.addEventListener("submit", submitForm); // Добавляем слушатель только на index.jsp

            const hiddenXInput = document.getElementById("hidden-x-input");
            document.querySelectorAll("#x-block input[type='button']").forEach(btn => {
                btn.classList.remove('active-x');
            });
            if (hiddenXInput) {
                hiddenXInput.value = "";
            }

            document.querySelectorAll("#x-block input[type='button']").forEach(button => {
                button.addEventListener("click", () => {
                    if (hiddenXInput) {
                        hiddenXInput.value = button.value;
                    }
                    document.querySelectorAll("#x-block input[type='button']").forEach(btn => {
                        btn.classList.remove('active-x');
                    });
                    button.classList.add('active-x');
                });
            });

            document.querySelectorAll("input[type='radio'][name='r']").forEach(radio => {
                radio.addEventListener('change', () => {
                    setTimeout(initCanvas, 0);
                });
            });

            document.querySelectorAll("#r-block input[type='radio'][name='r']").forEach(radio => {
                radio.checked = false;
            });
        }
    }

    if (typeof initCanvas === 'function') {
        initCanvas();
    } else {
        console.warn("initCanvas function not found. main.js might not be loaded yet.");
    }
});

// Глобальная функция для получения текущего выбранного R
function getR() {
    const rRadio = document.querySelector("input[type='radio'][name='r']:checked");
    if (rRadio) {
        return Number(rRadio.value);
    }

    // Если нет выбранного радио, проверяем currentClickedPoint (для resultPage.jsp)
    const isResultPage = window.location.pathname.includes("/resultPage.jsp");
    if (isResultPage && typeof currentClickedPoint !== 'undefined' && currentClickedPoint && currentClickedPoint.r) {
        // Если R взято из текущей точки, попробуем активировать соответствующее радио
        const rFromResult = currentClickedPoint.r;
        const matchingRadio = document.querySelector(`input[type="radio"][name="r"][value="${rFromResult.toFixed(1)}"]`);
        if (matchingRadio) {
            matchingRadio.checked = true; // Активируем его
            return Number(matchingRadio.value);
        }
    }

    // Если R не выбрано и нет текущей точки для подстановки, выбрасываем ошибку
}
function updateResultImage() {
    const resultImageElement = document.getElementById('resultImage');
    if (!resultImageElement) {
        console.warn("Element 'resultImage' not found.");
        return;
    }
    // Проверяем, есть ли данные о текущем результате
        if (currentClickedPoint.isHit) {
            resultImageElement.src = "${pageContext.request.contextPath}/styles/hit_image.jpg"; // Путь к картинке при попадании
            resultImageElement.alt = "Попадание в область";
        } else {
            resultImageElement.src = "${pageContext.request.contextPath}/styles/miss_image.jpg"; // Путь к картинке при промахе
            resultImageElement.alt = "Промах";
        }
}