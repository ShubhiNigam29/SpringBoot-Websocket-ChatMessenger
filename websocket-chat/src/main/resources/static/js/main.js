'use strict';

let usernamePage = document.querySelector('#username-page');
let chatPage = document.querySelector('#chat-page');
let usernameForm = document.querySelector('#usernameForm');
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');
let messageArea = document.querySelector('#messageArea');
let connectingElement = document.querySelector('.connecting');
let stompClient = null;
let username = null;

let colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').nodeValue.trim();
    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        let socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the public topic
    stompClient.subscribe('/topic/public', onMessageReceived);
    // Username is sent to the server
    stompClient.send("/app/chat.addUser",{},JSON.stringify({sender: username, type: 'JOIN'}));
    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    let msg = messageInput.nodeValue.trim();
    if(msg && stompClient) {
        let chatMessage = {
            sender: username,
            content: messageInput.nodeValue,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage",{},JSON.stringify(chatMessage));
        messageInput.nodeValue = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    let msg = JSON.parse(payload.body);
    let msgElement = document.createElement('li');
    if(msg.type === 'JOIN') {
        msgElement.classList.add('event-message');
        msg.content = msg.sender + 'joined!';
    } else if(msg.type === 'LEAVE') {
        msgElement.classList.add('event-message');
        msg.content = msg.sender + 'left the chat!';
    } else {
        msgElement.classList.add('chat-message');
        let avatarElement = document.createElement('i');
        let avatarText = document.createTextNode(msg.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(msg.sender);
        msgElement.appendChild(avatarElement);
        let usernameElement = document.createElement('span');
        let usernameText = document.createTextNode(msg.sender);
        usernameElement.appendChild(usernameText);
        msgElement.appendChild(usernameElement);
    }
    let textElement = document.createElement('p');
    let messageText = document.createTextNode(msg.content);
    textElement.appendChild(messageText);
    msgElement.appendChild(textElement);
    messageArea.appendChild(msgElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(msgSender) {
    let hash = 0;
    for(let i=0; i<msgSender.length;i++) {
        hash = 31*hash+msgSender.charCodeAt(i);
    }
    let index = Math.abs(hash%colors.length)
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);