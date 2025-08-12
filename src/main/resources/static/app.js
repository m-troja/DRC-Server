        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility
              = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
                const socket = new SockJS('http://localhost:8080/chat');
                stompClient = Stomp.over(socket);
                const from = document.getElementById('from').value; // pobierz wartość z pola `from`

            stompClient.connect(
                { username: from,
                role: 'ROLE_USER'
                 },  //  STOMP Header
                function (frame) {
                    console.log('Connected: ' + frame);
                    setConnected(true);
                    stompClient.subscribe('/client/messages', function (messageOutput) {
                        const message = JSON.parse(messageOutput.body);
                        showMessageOutput(message);

                        console.log("Received message:", message);
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
              JSON.stringify({'from':from, 'text':text}));
        }

        function showMessageOutput(messageOutput) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(messageOutput.from + ": "
              + messageOutput.text + " (" + messageOutput.time + ")"));
            response.appendChild(p);
        }
