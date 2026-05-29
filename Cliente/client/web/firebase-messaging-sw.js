importScripts("https://www.gstatic.com/firebasejs/10.8.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/10.8.0/firebase-messaging-compat.js");

const firebaseConfig = {
  apiKey: "AIzaSyB_EgaWbo7FJ6FVrzYgS2dt7jC2TaDwuvg",
  authDomain: "alarmaantiolvidos.firebaseapp.com",
  projectId: "alarmaantiolvidos",
  storageBucket: "alarmaantiolvidos.firebasestorage.app",
  messagingSenderId: "245217712334",
  appId: "1:245217712334:web:1d868fee9021f060b2189f"
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();