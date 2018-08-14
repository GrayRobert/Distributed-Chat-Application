<template>
    <div class="container">
        <div class="row">
            <div class="column">
                <div v-if="connected != 'Connected'" class="connecting" v-bind:style="{ color: connectingErrorColour }">
                        <span v-html="connected"></span>
                </div>
                <div v-if="showUserPage == true" class="chat-container">
                    <h1 class="title">Type your username</h1>
                    <form id="usernameForm" name="usernameForm">
                        <input v-model="userName" type="text" id="name" placeholder="Username" autocomplete="off" />
                        <button @click.prevent="login" class="accent username-submit">Start Chatting</button>
                    </form>
                </div>
                <div id="chat-page" v-else class="chat-container">
                    <ul id="messageArea">
                        <template v-for="message in messages">
                            <li v-bind:class="message.cssClass" :key="message.timeStamp">
                                <i v-if="message.type === 'CHAT'" v-bind:style="'background-color: ' + message.avatarColor">{{message.sender[0]}}</i>
                                <span>{{message.sender}}</span>
                                <p>{{ message.content }}</p>
                            </li>
                        </template>
                    </ul>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="column">
                <div v-if="showUserPage == false" class="messageInput">
                    <form id="messageForm" name="messageForm">
                        <div>
                            <input v-model="messageBody" type="text" id="message" placeholder="Type a message..." autocomplete="off">
                            <button @click.prevent="send" class="primary">Send</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

// CONSTANTS
const webSocketEndPoint = '//chatappus.com:8443/chat-socket';
const stompSendEndPoint = '/app/chat.sendMessage';
const stompSubscribeTopic = '/topic/public';
const stompAddUserEndPoint = '/app/chat.addUser';
const userColours = [
    '#800080','#872187','#9A03FE','#892EE4','#3923D6','#2966B8','#23819C','#BF00BF',
    '#BC2EBC','#A827FE','#9B4EE9','#6755E3','#2F74D0','#2897B7','#DB00DB','#D54FD5',
    '#B445FE','#A55FEB','#8678E9','#4985D6','#2FAACE','#F900F9','#DD75DD','#BD5CFE',
    '#AE70ED','#9588EC','#6094DB','#44B4D5'
];

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    const index = Math.abs(hash % userColours.length);
    return userColours[index];
}

export default {
    name: 'chat',
        data () {
        return {
            userName: null,
            connected: 'Connected',
            connectingErrorColour: 'green',
            showUserPage: true,
            stompClient: null,
            messages: [],
        }
    },
    // Important the websocket connection is opened when the component mounts
    created: function() {
            var socket = new SockJS(webSocketEndPoint);
            this.stompClient = Stomp.over(socket)
            //console.log("Connecting to API")
            this.stompClient.connect({}, (frame)=>{this.onConnected(frame)}, (error)=>{this.onError(error)})
            },
    methods: {
        send () {
            console.log("sending message " + this.messageBody)
            if(this.messageBody && this.stompClient) {
                let chatMessage = {
                    sender: this.userName,
                    content: this.messageBody,
                    type: 'CHAT'
                };
                this.stompClient.send(stompSendEndPoint, {}, JSON.stringify(chatMessage));
                this.messageBody = null;
            }
        },
        login () {
            // Send username to server
            this.stompClient.send(stompAddUserEndPoint, {},
                JSON.stringify({sender: this.userName, type: 'JOIN'})
            )
            // Hide the user page ToDO: Make conditional...
            this.showUserPage = false
        },
        onConnected: function(frame) {
            // Subscribe to public topic
            console.log(frame)
            this.connected = 'Connected'
            this.stompClient.subscribe(stompSubscribeTopic, (payload) => {this.onMessageReceived(payload)})
        },
        onMessageReceived: function(payload) {
            let message = JSON.parse(payload.body)
            if(message.type === 'JOIN') {
                message.content = message.sender + ' joined!'
                message.cssClass = 'event-message'
            } else if (message.type === 'LEAVE') {
                message.content = message.sender + ' left!'
                message.cssClass = 'event-message'
            } else {
                message.cssClass = 'chat-message'
                message.avatarColor = getAvatarColor(message.sender)
            }
            this.messages.push(message)
        },
        onError: function(error) {
            this.connected = 'Could not connect to messaging server. Please <strong>refresh</strong> this page to try again!'
            this.connectingErrorColour = 'red'
            console.log(error)
        },
    }
}
</script>

<style lang="less">
    
    body {
        margin: 0;
        padding: 0;
        background-color: #fff;
        height: 100%;
    }

    .container {
        top:35px;
        padding: 0px !important;
        height: 100%;
    }
    .chat-container {
        margin-left: auto;
        margin-right: auto;
        background-color: #fff;
        margin-top: 30px;
        height: calc(100%);
        position: relative;
        padding:30px 30px 0px 30px;
    }

    .chat-container .event-message {
        width: 100%;
        text-align: center;
        clear: both;
    }

    .chat-container .event-message p {
        color: #777;
        font-size: 14px;
        word-wrap: break-word;
    }

    .chat-container ul {
        list-style-type: none;
        background-color: #FFF;
        margin: 0;
        padding: 0 20px 0px 20px;
        height: calc(100% - 150px);
    }

    .chat-message {
        padding:20px;
        top:5px;
    }

    .chat-container ul li {
        line-height: 2rem;
        margin: 0;
        border-bottom: 1px solid #f4f4f4;
        left:60px;
    }

    .chat-container ul li p {
        margin: 0;
    }
    
    .chat-container .chat-message i {
        position: absolute;
        width: 42px;
        height: 42px;
        left: 30px;
        display: inline-block;
        vertical-align: middle;
        font-size: 18px;
        line-height: 42px;
        color: #fff;
        text-align: center;
        border-radius: 50%;
        font-style: normal;
        text-transform: uppercase;
    }

    .chat-container .chat-message span {
        color: #333;
        font-weight: 600;
        margin-left:30px;
    }

    .chat-container .chat-message p {
        color: #43464b;
        margin-left:30px;
    }

    #messageForm {
        padding:0px 30px 0px 30px;
        margin-top:20px;
        margin-bottom: 0px !important;
        position: fixed;
        bottom:0px;
        background-color: #FFF;
        width:100%;
    }

    #messageForm input {
        float: left;
        width:60%;
    }

    #messageForm button {
        float: left;
    }

    @media only screen and (max-width: 600px) {
        .chat-container ul {
            padding: 0;
        }
    }
    .connecting {
        margin-top:50px;
        display: block;
        padding:10px;
        color: #fff;
        background-color:rgb(248, 222, 252);
    }
</style>
