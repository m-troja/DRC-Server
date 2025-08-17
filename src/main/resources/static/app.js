var stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
    document.getElementById('questionResponse').innerHTML = '';
}

function connect() {
    const from = document.getElementById('from').value;
    const role = document.querySelector('input[name="role"]:checked')?.value;

    console.log("connect() called");
    console.log("Input username:", from);
    console.log("Selected role:", role);

    if (!from) {
        alert("Username is required!");
        console.error("connect() aborted: missing username");
        return;
    }

    if (!role) {
        alert("Role must be selected!");
        console.error("connect() aborted: missing role");
        return;
    }

    try {
        const socket = new SockJS(`http://localhost:8080/game?username=${encodeURIComponent(from)}&role=${encodeURIComponent(role)}`);
        stompClient = Stomp.over(socket);

        console.log("Attempting STOMP connection...");

        stompClient.connect({}, function(frame) {
            console.log("STOMP connected frame:", frame);
            if (frame && frame.headers) {
                setConnected(true); // Corrected this line
                console.log("STOMP version: " + frame.headers.version);
                console.log("STOMP server header:", frame.headers.server || "brak");
            }

            stompClient.subscribe('/client/messages', function (messageOutput) {
                console.log("Received /client/messages:", messageOutput.body);
                const message = JSON.parse(messageOutput.body);
                showMessageOutput(message);
            });

            stompClient.subscribe('/client/question', function (messageOutput) {
                console.log("Received /client/question:", messageOutput.body);
                const question = JSON.parse(messageOutput.body);
                showMessageOutput(question);
            });

            stompClient.subscribe('/client/ping', function (ping) {
                console.log("Received /client/ping:", ping.body);
                const pingMessage = JSON.parse(ping.body);
                showMessageOutput(pingMessage);

                logPing("⬅️ Ping received: " + JSON.stringify(pingMessage));

                const reply = {
                    text: "KeepAlive from client!",
                    date: new Date().toISOString()
                };

                stompClient.send("/server/ping", {}, JSON.stringify(reply));
                logPing("➡️ Ping sent: " + JSON.stringify(reply));
            });

        }, function (error) {
            console.error('STOMP connection error:', error);
            if (error && typeof error === 'object') {
                console.error('STOMP error details:', JSON.stringify(error));
            }
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
        console.log("Disconnecting stompClient...");
        stompClient.disconnect(function() {
            console.log("Disconnected callback called.");
            setConnected(false);
        });
    } else {
        console.log("No stompClient to disconnect.");
        setConnected(false);
    }
    console.log("Disconnect function finished.");
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

function fetchQuestion() {
    var id = document.getElementById('questionId').value;
    var withAnswers = document.getElementById('withAnswers').checked;

    stompClient.send("/server/question/", {}, JSON.stringify({
        'id': id,
        'withAnswers': withAnswers
    }));
    console.log("Fetched question id: ", id, ", withAnswers: " + withAnswers)
}

function logPing(message) {
    const pingLog = document.getElementById("pingLog");
    const p = document.createElement("p");
    p.textContent = message;
    pingLog.appendChild(p);
    pingLog.scrollTop = pingLog.scrollHeight;
}
