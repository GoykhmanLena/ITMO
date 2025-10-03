// main.js - логика канваса и точек

const SCALE_FACTOR = 40; // Масштаб: 1 единица = 40 пикселей
const CANVAS_SIZE = 400; // Размер канваса в пикселях (400x400)
const AXIS_OFFSET = 10; // Отступ для меток осей

// Глобальная функция initCanvas (больше не export, так как напрямую включается)
function initCanvas() {
    const canvas = document.getElementById("graphCanvas");
    if (!canvas) { // Проверяем, существует ли канвас на текущей странице
        console.warn("Canvas element 'graphCanvas' not found. Skipping canvas initialization.");
        return;
    }
    const ctx = canvas.getContext("2d");

    canvas.width = CANVAS_SIZE;
    canvas.height = CANVAS_SIZE;

    // Сброс трансформации перед рисованием, чтобы начать с чистого листа
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const isResultPage = window.location.pathname.includes("/controller");

    if (!isResultPage) {
        // Только для index.jsp
        const oldClickHandler = canvas._canvasClickHandler; // Сохраняем ссылку на старый обработчик
        if (oldClickHandler) {
            canvas.removeEventListener("click", oldClickHandler);
        }

        // Добавляем новый обработчик кликов
        const newClickHandler = function handleCanvasClick(event) {
            const errorDiv = document.getElementById("error");
            if (errorDiv) {
                errorDiv.hidden = true; // Скрываем ошибки перед новой попыткой
                errorDiv.innerText = "";
            }


            try {
                const r = getR(); // Получаем R. Если не выбрано, выбросит ошибку.

                const rect = canvas.getBoundingClientRect();
                // Переводим DOM координаты (относительно окна) в координаты канваса (относительно элемента canvas)
                const clientX = event.clientX - rect.left;
                const clientY = event.clientY - rect.top;

                // Координаты относительно центра канваса
                const xCanvas = clientX - canvas.width / 2;
                const yCanvas = canvas.height / 2 - clientY; // Ось Y в канвасе инвертирована относительно математической

                // Масштабируем пиксельные координаты к реальным X, Y
                const x = xCanvas / SCALE_FACTOR;
                const y = yCanvas / SCALE_FACTOR;

                console.log(`Clicked on canvas: x=${x.toFixed(2)}, y=${y.toFixed(2)}, r=${r}`);

                // Обновляем форму и отправляем ее
                sendPoint(x, y, r);

            } catch (e) {
                if (errorDiv) {
                    errorDiv.hidden = false;
                    errorDiv.innerText = e.message;
                }
                console.error("Canvas click error:", e);
            }
        };
        canvas.addEventListener("click", newClickHandler);
        canvas._canvasClickHandler = newClickHandler;
    }
    // Удаляем старый обработчик, если он есть, чтобы избежать дублирования
    // (Это важно при переинициализации canvas, например, при изменении R)
    // Используем `removeEventListener` с той же функцией, если это возможно,
    // или создаем новую функцию-обертку, чтобы корректно удалить старый.
    // Проще всего на каждой инициализации удалять все и вешать новый.
     // Сохраняем ссылку на новый обработчик для последующего удаления



    try {
        // Загружаем точки из JSP (глобальная переменная clickedPoints из graph.jsp)
        // loadPoints() должна быть определена в graph.jsp и вызывается здесь
        if (typeof loadPoints === 'function') {
            loadPoints(); // Заполняем глобальный массив clickedPoints
        } else {
            console.warn("loadPoints function not found. graph.jsp might not be included or loaded yet.");
        }

        let rValue = 1;
        if (isResultPage && currentClickedPoint) {
            rValue = currentClickedPoint.r;
        } else {
            try {
                rValue = getR(); // Получаем текущее выбранное значение R
            } catch (e) {
                const errorDiv = document.getElementById("error");
                if (errorDiv) {
                    errorDiv.hidden = false;
                    errorDiv.innerText = e.message;
                }
                console.error("Error getting R for canvas initialization:", e.message);
            }
        }

        // Передаем масштабированный R для рисования
        drawShape(ctx, canvas, clickedPoints, rValue * SCALE_FACTOR);

    } catch (e) {
        console.error("Error initializing canvas or fetching points:", e.message);
        // Если R не выбрано или произошла другая ошибка при инициализации,
        // рисуем график с R=1 по умолчанию, но без точек, и показываем ошибку.
        drawShape(ctx, canvas, [], 1 * SCALE_FACTOR); // Используем 1 как дефолтное R для масштабирования
        const errorDiv = document.getElementById("error");
        if (errorDiv) {
            errorDiv.hidden = false;
            errorDiv.innerText = e.message; // Показываем ошибку, например, если R не выбрано
        }
    }
}

// Эта функция должна обновить скрытые поля формы и вызвать submit
function sendPoint(x, y, r) {
    const form = document.getElementById("data-form");
    if (!form) {
        console.error("Data form not found. Cannot send point.");
        return;
    }

    const hiddenXInput = document.getElementById("hidden-x-input");
    if (!hiddenXInput) {
        console.error("Hidden X input field not found!");
        return;
    }
    hiddenXInput.value = x.toFixed(2); // Округляем для отправки

    const yInput = form.querySelector("input[name='y']");
    if (!yInput) {
        console.error("Y input field not found!");
        return;
    }
    yInput.value = y.toFixed(2); // Округляем для отправки

    // Убедимся, что выбранное R соответствует R из графика
    const rRadio = form.querySelector(`input[type="radio"][name="r"][value="${r.toFixed(1)}"]`);
    if (rRadio) {
        rRadio.checked = true;
    } else {
        // Если R с канваса не соответствует ни одной радио-кнопке, это проблема
        throw new Error(`R value ${r.toFixed(1)} from click not found in form options.`);
    }

    form.submit();
}

function drawShape(ctx, canvas, points, R_scaled) { // R_scaled - это R * SCALE_FACTOR
    ctx.setTransform(1, 0, 0, 1, 0, 0); // Сброс трансформации, чтобы избежать наложений
    ctx.clearRect(0, 0, canvas.width, canvas.height); // Очистка канваса

    // Перемещаем начало координат в центр
    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1); // Инвертируем ось Y, чтобы она смотрела вверх

    // ----- Рисование фигуры -----
    ctx.fillStyle = "rgba(51, 153, 255, 0.7)"; // Цвет для фигуры (полупрозрачный синий)

    // Область 1: Треугольник в первом квадранте (x >= 0, y >= 0)
    // Уравнение: y <= (r/2) - (x/2)  => 2y <= r - x => x + 2y <= r
    // Точки: (0, 0), (0, R/2), (R, 0)
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(0, R_scaled / 2); // (0, R/2)
    ctx.lineTo(R_scaled, 0); // (R, 0)
    ctx.closePath();
    ctx.fill();

    // Область 2: Прямоугольник во втором квадранте (x >= 0, y <= 0, x <= R, y >= -R/2)
    // Точки: (0, 0), (R, 0), (R, -R/2), (0, -R/2)
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(R_scaled, 0); // (R, 0)
    ctx.lineTo(R_scaled, -R_scaled / 2); // (R, -R/2)
    ctx.lineTo(0, -R_scaled / 2); // (0, -R/2)
    ctx.closePath();
    ctx.fill();

    // Область 3: Четверть круга в третьем квадранте (x <= 0, y <= 0, x*x + y*y <= r*r)
    ctx.beginPath();
    ctx.arc(0, 0, R_scaled, Math.PI, Math.PI * 3 / 2); // Круг радиусом R
    ctx.lineTo(0, 0); // Соединяем с центром
    ctx.closePath();
    ctx.fill();

    // ----- Рисование осей -----
    ctx.strokeStyle = "white";
    ctx.lineWidth = 1;

    // Ось X
    ctx.beginPath();
    ctx.moveTo(-canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.stroke();

    // Стрелка для оси X
    ctx.beginPath();
    ctx.moveTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2 - 5, 5);
    ctx.moveTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2 - 5, -5);
    ctx.stroke();
    ctx.closePath();

    // Ось Y
    ctx.beginPath();
    ctx.moveTo(0, -canvas.height / 2);
    ctx.lineTo(0, canvas.height / 2);
    ctx.stroke();

    // Стрелка для оси Y
    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(5, canvas.height / 2 - 5);
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(-5, canvas.height / 2 - 5);
    ctx.stroke();
    ctx.closePath();


    // ----- Рисование меток для осей -----
    ctx.scale(1, -1); // Возвращаем Y в нормальное состояние для текста
    ctx.fillStyle = "white";
    ctx.font = "12px Arial";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";

    const currentR = R_scaled / SCALE_FACTOR; // Получаем фактическое R для меток

    // Метки по X
    ctx.fillText("X", canvas.width / 2 - AXIS_OFFSET, AXIS_OFFSET); // Метка оси X
    ctx.fillText("0", 0, AXIS_OFFSET); // Метка 0
    ctx.fillText(`${currentR / 2}`, R_scaled / 2, AXIS_OFFSET);
    ctx.fillText(`${currentR}`, R_scaled, AXIS_OFFSET);
    ctx.fillText(`${-currentR / 2}`, -R_scaled / 2, AXIS_OFFSET);
    ctx.fillText(`${-currentR}`, -R_scaled, AXIS_OFFSET);

    // Метки по Y
    ctx.fillText("Y", AXIS_OFFSET, canvas.height / 2 - AXIS_OFFSET); // Метка оси Y
    ctx.fillText(`${currentR / 2}`, AXIS_OFFSET, -R_scaled / 2);
    ctx.fillText(`${currentR}`, AXIS_OFFSET, -R_scaled);
    ctx.fillText(`${-currentR / 2}`, AXIS_OFFSET, R_scaled / 2);
    ctx.fillText(`${-currentR}`, AXIS_OFFSET, R_scaled);


    // ----- Рисование точек -----
    points.forEach((point) => {
        // Убедимся, что рисуем точки для текущего R (или близкого к нему)
        // Иначе на графике с R=1 будут точки, рассчитанные для R=3
        // Приведение R_scaled / SCALE_FACTOR к фиксированному числу, например, toFixed(1)
        // point.r - это реальное R точки, а currentR - текущее R графика
        if (Math.abs(point.r - currentR) < 0.01) { // Сравниваем с небольшой погрешностью
            const { x, y, isHit } = point;

            ctx.beginPath();
            // Инвертируем y для рисования, так как на канвасе y-ось направлена вниз
            ctx.arc(x * SCALE_FACTOR, -y * SCALE_FACTOR, 4, 0, Math.PI * 2);
            ctx.fillStyle = isHit ? "green" : "red"; // Красим точки в зависимости от результата
            ctx.fill();
            ctx.strokeStyle = "white";
            ctx.lineWidth = 1;
            ctx.stroke();
            ctx.closePath();
        }
    });

    if (currentClickedPoint) {
        const { x, y, isHit } = currentClickedPoint;
        ctx.beginPath();
        ctx.arc(x * SCALE_FACTOR, -y * SCALE_FACTOR, 4, 0, Math.PI * 2);
        ctx.fillStyle = isHit ? "green" : "red";
        ctx.fill();
        ctx.strokeStyle = "white";
        ctx.lineWidth = 1;
        ctx.stroke();
        ctx.closePath();
    }

    // Сброс всех трансформаций после рисования
    ctx.setTransform(1, 0, 0, 1, 0, 0);
}