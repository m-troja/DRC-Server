var stompClient = null;
let pingInterval = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('questionResponse').innerHTML = '';
    document.getElementById('answersResponse').innerHTML = '';
}


function startAutoPing() {
    if (!stompClient || !stompClient.connected) return;

    // send ping every 10 seconds
    pingInterval = setInterval(() => {
        const pingMessage = {
            ping: Date.now()
        };
        stompClient.send("/server/ping", {}, JSON.stringify(pingMessage));
        logPing("➡️ Ping sent: " + JSON.stringify(pingMessage));
    }, 10000); // 10000 ms = 10 s
}

function stopAutoPing() {
    if (pingInterval) {
        clearInterval(pingInterval);
        pingInterval = null;
    }
}

function connect() {
    const from = document.getElementById('from').value;
    const role = document.querySelector('input[name="role"]:checked')?.value;

    if (!from) {
        alert("Username is required!");
        return;
    }
    if (!role) {
        alert("Role must be selected!");
        return;
    }
    try {
        const socket = new SockJS(`http://172.167.100.49:8081/game?username=${encodeURIComponent(from)}&role=${encodeURIComponent(role)}`);
        stompClient = Stomp.over(socket);

            stompClient.connect({}, function(frame) {
                setConnected(true);
                startAutoPing();

            stompClient.subscribe('/client/question', function (messageOutput) {
                const question = JSON.parse(messageOutput.body);
                showQuestion(question);
            });

            stompClient.subscribe('/user/' + from + '/queue/admin-event', function (messageOutput) {
                            const question = JSON.parse(messageOutput.body);
                            showQuestion(messageOutput.body);
                        });

//
//            stompClient.subscribe('/user/' + from + '/answer', function (messageOutput) {
//                const answers = JSON.parse(messageOutput.body);
//                showAnswers(answers);
//            });

            stompClient.subscribe('/user/' + from + '/queue/all-answers', function (messageOutput) {
                const answers = JSON.parse(messageOutput.body);
                showAnswers(answers);
            });

            stompClient.subscribe('/user/' + from + '/queue/answer', function (messageOutput) {
                const answer = JSON.parse(messageOutput.body);
                    showRequestedAnswer(answer);

            });

            stompClient.subscribe('/user/' + from + '/queue/kick', function (messageOutput) {
                try {
                    const kickPayload = JSON.parse(messageOutput.body);

                    alert("You were disconnected by the server.");

                    stopAutoPing();

                    stompClient.disconnect(() => {
                        setConnected(false);
                        console.log("Disconnected due to server kick.");
                    });
                } catch (e) {
                    console.error("Failed to handle kick message:", e);
                }
            });

        }, function (error) {
            console.error('STOMP connection error:', error);
            alert("Connection error: Check console for details");
            setConnected(false);
        });
    } catch (ex) {
        console.error("Exception during STOMP connect:", ex);
        alert("Exception during connect: " + ex.message);
        setConnected(false);
    }
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect(function() {
            setConnected(false);
        });
    } else {
        setConnected(false);
    }
}

function sendMessage() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('text').value;
    stompClient.send("/server/chat", {}, JSON.stringify({
        'from': from,
        'text': text
    }));
}

function showMessageOutput(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": "
        + messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
}

function showQuestion(question) {
    const container = document.getElementById('questionResponse');
    container.innerHTML = "";

    const h3 = document.createElement("h3");
    h3.textContent = "Question " + question.id;
    container.appendChild(h3);

    const p = document.createElement("p");
    p.textContent = question.text;
    container.appendChild(p);

    document.getElementById('answersResponse').innerHTML = "";
}

function showAnswers(answers) {
    const container = document.getElementById('answersResponse');
    container.innerHTML = "";

    console.log("Answers received:", answers);

    if (!Array.isArray(answers) || answers.length === 0) {
        container.textContent = "No answers";
        return;
    }

    // Create table
    const table = document.createElement("table");
    table.border = "1";
    table.style.borderCollapse = "collapse";

    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    ["#", "Text", "Value"].forEach(header => {
        const th = document.createElement("th");
        th.textContent = header;
        th.style.padding = "4px";
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    const tbody = document.createElement("tbody");
    answers.forEach((answer, index) => {
        const row = document.createElement("tr");

        const numberCell = document.createElement("td");
        numberCell.textContent = index + 1;

        const textCell = document.createElement("td");
        textCell.textContent = answer?.text ?? "(no text)";

        const valueCell = document.createElement("td");
        valueCell.textContent = answer?.value ?? "(no value)";

        [numberCell, textCell, valueCell].forEach(cell => {
            cell.style.padding = "4px";
            row.appendChild(cell);
        });

        tbody.appendChild(row);
    });

    table.appendChild(tbody);
    container.appendChild(table);
}
function logPing(message) {
    const pingLog = document.getElementById("pingLog");
    const p = document.createElement("p");
    p.textContent = message;
    pingLog.appendChild(p);
    pingLog.scrollTop = pingLog.scrollHeight;
}

function fetchQuestion() {
    var id = document.getElementById('questionId').value;
    var withAnswers = document.getElementById('withAnswers').checked;

    stompClient.send("/server/question/", {}, JSON.stringify({
        'id': id,
        'withAnswers': withAnswers
    }));
    console.log("Fetched question id: ", id, ", withAnswers: " + withAnswers)
}

function requestAnswer() {
    var answerQuestestGameId = document.getElementById('answerQuestestGameId').value;
    var answerQuestestValue = document.getElementById('answerQuestestValue').value;

    stompClient.send("/server/answer", {}, JSON.stringify({
        'value': answerQuestestValue,
        'gameId': answerQuestestGameId
    }));
    console.log("Requested game id: ", answerQuestestGameId, ", value: " + answerQuestestValue)
}

function showRequestedAnswer(answer) {
    const container = document.getElementById('requestedAnswersResponse');
    container.innerHTML = "";

    const h3 = document.createElement("h3");
    h3.textContent = "Requested Answer";
    container.appendChild(h3);

    const p = document.createElement("p");
    p.textContent = answer.text + " → " + answer.value + " PLN ";
    container.appendChild(p);
}