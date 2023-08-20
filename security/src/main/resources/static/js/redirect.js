const token = localStorage.getItem('access_token');
const expirationTime = localStorage.getItem('expiration_time');
const currentTime = Date.now();

if(!(token && expirationTime &&
    currentTime < expirationTime)) {
    window.location.href = 'login';
}