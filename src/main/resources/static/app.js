        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility
              = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
            document.getElementById('questionResponse').innerHTML = '';

        }

        function connect() {
                const socket = new SockJS('http://localhost:8080/chat');
                stompClient = Stomp.over(socket);
                const from = document.getElementById('from').value;

            stompClient.connect(
                {   username: from,
                    role: 'ROLE_USER'
                 },  //  STOMP Header

                function (frame) {
                    console.log('Connected: ' + frame);
                    setConnected(true);

                    // Subscriptions
                    stompClient.subscribe('/client/messages', function (messageOutput) {
                        const message = JSON.parse(messageOutput.body);
                        showMessageOutput(message);
                        console.log("showing output message: " + message);
                    });

                    stompClient.subscribe('/client/question', function (messageOutput) {
                        const question = JSON.parse(messageOutput.body);
                        showMessageOutput(question);
                        console.log("showing output question: " + question);
                    });

                    stompClient.subscribe('/client/ping', function (ping) {
                                const pingMessage = JSON.parse(ping.body);
                                console.log("Ping received from server: " + pingMessage);
                                showMessageOutput(pingMessage);

                                console.log("Ping: " + pingMessage);
                                 stompClient.send("/server/ping", {}, JSON.stringify({
                                       text: "KeepAlive from client!",
                                       date: new Date().toISOString()
                               }));
                    });
                },
                function (error) {
                    console.error('Connection error:', error);
                }
            );
        }

        function disconnect() {
            if(stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        function sendMessage() {
            var from = document.getElementById('from').value;
            var text = document.getElementById('text').value;
            stompClient.send("/server/chat", {},
            JSON.stringify(
            {
                'from':from,
                'text':text
            }
            ));
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

            stompClient.send("/server/question/", {},  JSON.stringify (
            {
                'id':id,
                'withAnswers':withAnswers
            }
            ));
            console.log("Fetched question id: ", id, ", withAnswers: " + withAnswers)
       }
