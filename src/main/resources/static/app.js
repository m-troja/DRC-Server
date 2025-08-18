var stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('questionResponse').innerHTML = '';
    document.getElementById('answersResponse').innerHTML = '';
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
        const socket = new SockJS(`http://localhost:8080/game?username=${encodeURIComponent(from)}&role=${encodeURIComponent(role)}`);
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function(frame) {
            setConnected(true);

            stompClient.subscribe('/client/question', function (messageOutput) {
                const question = JSON.parse(messageOutput.body);
                showQuestion(question);
            });

            stompClient.subscribe('/user/' + from + '/answer', function (messageOutput) {
                const answers = JSON.parse(messageOutput.body);
                showAnswers(answers);
            });

//   Client listens to private channel - to get answers
            stompClient.subscribe('/user/' + from + '/queue/answer', function (messageOutput) {
                const answers = JSON.parse(messageOutput.body);
                showAnswers(answers);
            });

//            stompClient.subscribe('/user/test/queue/answer', function (messageOutput) {
//                    const answers = JSON.parse(messageOutput.body);
//                    showAnswers(answers);
//                });
//             stompClient.subscribe('/user/client/answer', function (messageOutput) {
//                             const answers = JSON.parse(messageOutput.body);
//                             showAnswers(answers);
//                         });
//                stompClient.subscribe('/queue/client/answer', function (messageOutput) {
//                                const answers = JSON.parse(messageOutput.body);
//                                showAnswers(answers);
//                            });
                stompClient.subscribe('/client/ping', function (ping) {
                const pingMessage = JSON.parse(ping.body);
                logPing("⬅️ Ping received: " + JSON.stringify(pingMessage));

                const reply = {
                    text: "KeepAlive from client!",
                    date: new Date().toISOString()
                };

//                stompClient.send("/server/ping", {}, JSON.stringify(reply));
//                logPing("➡️ Ping sent: " + JSON.stringify(reply));
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
    console.log("Answer received " + answers)
    if (!answers || answers.length === 0) {
        container.textContent = "No answers";
        return;
    }

    const ul = document.createElement("ul");
    answers.forEach(ans => {
        const li = document.createElement("li");
        li.textContent = ans.text;
        ul.appendChild(li);
    });
    container.appendChild(ul);
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