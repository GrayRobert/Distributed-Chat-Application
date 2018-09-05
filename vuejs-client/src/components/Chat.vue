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
                        <template v-for="message in orderedMessages">
                            <li v-bind:class="message.cssClass" :key="message.timeStamp">
                                <i v-if="message.type === 'CHAT'" v-bind:style="'background-color: ' + message.avatarColor">{{message.sender[0]}}</i>
                                <span v-if="message.type === 'CHAT'">{{message.sender}}</span>
                                <p>{{ message.content }}</p>
                                <span class="time">{{message.sent | moment("dddd, MMMM Do YYYY HH:mm:ss") }} <span v-if="message.type === 'CHAT'" class="tick-green">✓</span></span>
                            </li>
                        </template>
                        <template v-for="message in filteredUnsentMessages">
                            <li v-bind:class="message.cssClass" :key="message.timeStamp">
                                <i v-if="message.type === 'CHAT'" v-bind:style="'background-color: ' + message.avatarColor">{{message.sender[0]}}</i>
                                <span v-if="message.type === 'CHAT'">{{message.sender}}</span>
                                <p>{{ message.content }}</p>
                                <span class="time">{{message.sent | moment("dddd, MMMM Do YYYY HH:mm:ss") }} <span v-if="message.type === 'CHAT'" class="tick-orange">✓</span></span>
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
//----------> IMPORTS <----------//
//import SockJS from 'sockjs-client'
//import Stomp from 'stompjs'
import StompWebsocket from '@stomp/stompjs'
import _ from 'lodash'
import md5 from 'md5'


//----------> CONSTANTS <----------//
//const webSocketEndPoint = '//chatappus.com:8443/chat-socket'
//const clientApiTimeOut = 60000; //Time out before API will kick the client off
const webSocketEndPoint = 'ws://localhost:8090/chat-socket'
const stompSendEndPoint = '/app/chat.sendMessage'
const stompSubscribeTopic = '/topic/public'
const stompAddUserEndPoint = '/app/chat.addUser'
const stompMissedMessagesEndPoint = '/app/chat.getMissedMessages'
const userColours = [
    '#800080', '#B445FE','#A55FEB','#8678E9','#4985D6','#2FAACE','#F900F9','#DD75DD','#BD5CFE','#AE70ED'
]


//----------> SOME HELPER FUNCTIONS <----------//
function getUserColour(messageSender) {
    let colour = 0;
    for (let i = 0; i < messageSender.length; i++) {
        colour = 10 * colour + messageSender.charCodeAt(i);
    }
    const index = Math.abs(colour % userColours.length);
    return userColours[index];
}
//Source: https://stackoverflow.com/questions/1988349/array-push-if-does-not-exist/24001122
Array.prototype.inArray = function(comparer) { 
    for(var i=0; i < this.length; i++) { 
        if(comparer(this[i])) return true; 
    }
    return false; 
};
Array.prototype.pushIfNotExist = function(element, comparer) { 
    if (!this.inArray(comparer)) {
        this.push(element);
    }
};


//----------> VUE <----------//
export default {
    name: 'chat',
        data() {
        return {
            userName: null,
            connected: 'Connected',
            connectingErrorColour: 'green',
            showUserPage: true,
            socket: null,
            stompClient: null,
            messages: [],
            resendMessages: [],
            messageBody: '',
            isConnected: false,
            bottom: 'bottom'
        }
    },
    computed: {
        //----------> Order Message By Sent Time <----------//
        orderedMessages: function () {
            // first order message by ascending sent time
            let messages = _.orderBy(this.messages, 'sent', 'ASC')

            messages.sort(function (message1, message2) {
            // If the first item has a higher number, move it down
            // If the first item has a lower number, move it up
            if ((message2.lastMessageId && message1.lastMessageId) && (message2.lastMessageId == message1.lastMessageId)) {return 0;}
            if (message2.lastMessageId && message2.lastMessageId > message1.id) {return -1;}
            if (message2.lastMessageId && message2.lastMessageId < message1.id) {return 1;}
            if (message2.id > message1.id) {return -1;}
            if (message2.id < message1.id) {return 1;}
            });
            return messages
        },
        //----------> Order Messages to be resent by sent <----------//
        orderedResendMessages: function () {
            return _.orderBy(this.resendMessages, 'sent', 'ASC')
        },
        filteredUnsentMessages: function () {
            // Show unsent messages list if we are not connected
            // To Do: REVIEW BETTER ALTERNATIVE
            if(!this.isConnected) {
                return _.orderBy(this.resendMessages, 'sent', 'ASC')
            } else {
                return null
            }
        },
        getLastChatMessage: function () {
            // Return Last Chat Message
                let orderedChatMessages = _.map(this.orderedMessages, function(o) {
                    if (o.type == "CHAT") return o
                })
                orderedChatMessages = _.without(orderedChatMessages, undefined)
                return _.last(orderedChatMessages)
        }
    },
    //----------> Open Connection To API as Page Mounts <----------//
    created: function() {
                this.connectToAPI()
            },
    methods: {
        send() {
            if(this.messageBody) {

                //----------> Get message ready to send <----------//
                console.log("Last message is: " + this.getLastChatMessage.id)

                let chatMessage = {
                    sender: this.userName,
                    content: this.messageBody,
                    sent: new Date(),
                    type: 'CHAT',
                    hash: '',
                    lastMessageId: this.getLastChatMessage.id
                }

                // create unique hash of message for verification and deduplication
                let messageHash = md5(chatMessage.sender+chatMessage.content+chatMessage.sent.toString()+chatMessage.type+Math.random())
                chatMessage.hash = messageHash
                
                //----------> start a transaction <----------//
                let tx = this.stompClient.begin()

                //----------> send message <----------//
                this.stompClient.send(stompSendEndPoint, {transaction: tx.id}, JSON.stringify(chatMessage))
                console.log("sending message " + this.messageBody)

                //----------> commit the transaction if we still have a connection <----------//
                if(this.isConnected) {
                    tx.commit();
                    console.log("Transaction commited, with id: " + tx.id)
                } else {
                    tx.abort();
                    console.log("Transaction aborted, with id: " + tx.id)
                    //----------> Store messages and send later <----------//
                    chatMessage.cssClass = 'chat-message'
                    chatMessage.avatarColor = getUserColour(chatMessage.sender)
                    this.resendMessages.push(chatMessage)
                }
                //----------> end transaction <----------//
                this.messageBody = null;
                window.scrollTo(0,document.body.scrollHeight+1000)
            }
        },
        reSendMessages() {
            if(this.isConnected && this.resendMessages.length > 0) {
                console.log("Retrying Messages: ")
                const resendChatMessages = this.orderedResendMessages

                try {
                    for (const key of Object.keys(resendChatMessages)) {
                        //----------> start a transaction <----------//
                        let tx = this.stompClient.begin();
                        console.log("Re-sending messages: " + key, resendChatMessages[key]);
                        let chatMessage = resendChatMessages[key]
                        chatMessage.reSent = new Date();
                        this.stompClient.send(stompSendEndPoint, {transaction: tx.id}, JSON.stringify(chatMessage))
                        //----------> commit the transaction if we still have a connection <----------//
                        if(this.isConnected) {
                            console.log("Transaction commited, with id: " + tx.id)
                            tx.commit();
                        } else {
                            console.log("Transaction aborted, with id: " + tx.id)
                            tx.abort();
                        }
                        //----------> end transaction <----------//
                    }
                    this.resendMessages = []
                } catch(e) {
                    console.log("Could not re-send messages: " + e)
                }
                
            }
        },
        getMissedMessages() {
            //----------> start a transaction <----------//
            let tx = this.stompClient.begin();
            // Send username to server
            this.stompClient.send(stompMissedMessagesEndPoint, {transaction: tx.id},
                JSON.stringify({sender: this.userName, type: 'MISSED_MESSAGES', sent: new Date()})
            )
            //----------> commit the transaction if we still have a connection <----------//
            if(this.isConnected) {
                console.log("Transaction commited, with id: " + tx.id)
                tx.commit();
            } else {
                console.log("Transaction aborted, with id: " + tx.id)
                tx.abort();
            }
        },
        connectToAPI: function() {
            if(!this.isConnected) {
                try {
                    this.stompClient = StompWebsocket.client(webSocketEndPoint)
                    this.stompClient.heartbeat.outgoing = 10000;
                    this.stompClient.heartbeat.incoming = 10000;
                    this.stompClient.connect({}, (frame)=>{this.onConnected(frame)}, (error)=>{this.onError(error)})
                    this.stompClient.reconnect_delay = 5000;
                } catch(e) {
                    console.log("Coundn't connect to API " + e)
                }
            }
        },
        login() {
            //----------> start a transaction <----------//
            let tx = this.stompClient.begin();
            // Send username to server
            this.stompClient.send(stompAddUserEndPoint, {transaction: tx.id},
                JSON.stringify({sender: this.userName, type: 'JOIN', sent: new Date()})
            )
            //----------> commit the transaction if we still have a connection <----------//
            if(this.isConnected) {
                tx.commit();
                console.log("Transaction commited, with id: " + tx.id)
            } else {
                tx.abort();
                console.log("Transaction aborted, with id: " + tx.id)
            }
            //----------> end transaction <----------//
            // Hide the user page ToDO: Make conditional...
            this.showUserPage = false
            this.getMissedMessages()
        },
        onConnected: function(frame) {
            console.log("Connected Frame is: " + frame)
            this.connected = 'Connected'
            this.isConnected = true
            //----------> Resend Unsent Messages <----------//
            this.reSendMessages()
            //----------> Get Missed Messages <----------//
            if(this.userName !=null) {
                this.getMissedMessages()
            }
            //----------> Subscribe to braodcast channel <----------//
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
                message.avatarColor = getUserColour(message.sender)
            }

            if(message.recipient === this.userName && message.type === 'TIME') {
                message.cssClass = 'event-message'
                this.messages.push(message)
            }

            //----------> Show messages for this client or broadcast mesages <----------//
            if(message.recipient === this.userName && message.type === 'CHAT') {
                if (!message.hash) {
                    this.messages.pushIfNotExist(message, function(e) { 
                        return e.id === message.id; 
                    });
                } else {
                    this.messages.pushIfNotExist(message, function(e) { 
                        return e.hash === message.hash; 
                    });
                }
            } else if (message.recipient === null && message.content != null) {
                this.messages.push(message)
            }

            //----------> Supposed to always scroll to the bottom: FIX <----------//
            window.scrollTo(0,document.body.scrollHeight+1000);
        },
        onError: function(error) {
            this.connected = 'Trying to establish connection to messaging server...'
            this.connectingErrorColour = 'red'
            console.log(error)
            this.isConnected = false
        }
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
        margin-bottom:30px;
        height: calc(100%);
        position: relative;
        padding:30px 30px 0px 30px;
        overflow-y: scroll;
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

    .time {
        font-size: 10px;
        color:#777 !important;
    }

    .tick-orange {
        color: orange !important;
        font-size: 14px;
    }

    .tick-green {
        color: green !important;
        font-size: 14px;
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
