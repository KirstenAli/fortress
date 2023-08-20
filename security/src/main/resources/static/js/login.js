import { loginModal} from '/js/loginmodal.js';
import { sendResetTokenModal} from '/js/sendResetTokenModal.js';
import { resetPasswordModal} from '/js/resetPasswordModal.js';
import {post, processResponse, passwordsMatch, createModalObj, setUpMessageModal} from '/js/utils.js';

let loginModalObj;
let sendResetTokenModalObj;
let resetPasswordModalObj;
let messageModalObj;
let messageModalId = setUpMessageModal('app');
export let redirectUrl = {redirectUrl:""};

function checkAccessToken() {
    const accessToken = localStorage.getItem('access_token');
    const expirationTime = localStorage.getItem('expiration_time');
    const currentTime = Date.now();

    if (accessToken && expirationTime &&
        currentTime < expirationTime) {

        console.log('Token is valid');
        redirect(redirectUrl);
    }

    loginModalObj.show();
}

async function handleLoginFormSubmit(authRequest) {
    const endpoint = '/auth/getToken';

    const response = await post(endpoint, authRequest, false);
    const status = await processResponse(response, 'New token obtained', messageModalObj);

    if(status.success){
        const token = await response.json();
        storeToken(token.accessToken, token.expiration);
    }

    return status;
}

async function resetPasswordWithToken(userDTO){
    const endpoint = '/user/public/resetPasswordWithToken';
    const successMessage = 'Password reset successful :)';

    let response = await post(endpoint, userDTO, false);

    const status = await processResponse(response, successMessage, messageModalObj);

    if(status.success)
        resetPasswordModalObj.hide();

    return status;
}

async function sendPasswordResetToken(userDTO) {
    sendResetTokenModalObj.hide();

    const endpoint = '/user/public/sendPasswordResetToken';
    const successMessage = 'A password reset token has been sent to ' + userDTO.username;

    let response = await post(endpoint, userDTO, false);

    let status = await processResponse(response, successMessage, messageModalObj);

    if (status.success){
        resetPasswordModalObj.show();
    }

    return status;
}

function storeToken(newAccessToken, expiration){
    localStorage.setItem('access_token', newAccessToken);
    localStorage.setItem('expiration_time', expiration);

    loginModalObj.hide();

    console.log('Token saved in local storage');
    redirect(redirectUrl);
}

function redirect(url){
    window.location.href = redirectUrl.redirectUrl;
}

function setUp() {

    Vue.createApp({
        data() {
            return {
                userDTO: {
                    username: '',
                    password:'',
                    passwordResetCode:''
                },
                repeatPassword:'',

                authRequest: {
                    username: '',
                    password:'',
                },
                message:''
            };
        },

        methods: {
            async handleLoginFormSubmit() {
                const status = await handleLoginFormSubmit(this.authRequest);
                this.message = status.message;
            },

            showSendResetTokenModal(){
                sendResetTokenModalObj.show();
            },

            async sendPasswordResetToken() {
                const status = await sendPasswordResetToken(this.userDTO);
                this.message = status.message;
            },

            async resetPasswordWithToken(){
                if(passwordsMatch(this.userDTO.password, this.repeatPassword)) {
                    const status = await resetPasswordWithToken(this.userDTO);
                    this.message = status.message;

                } else {
                    this.message="Passwords do not match";
                }
            }
        },

        async created() {

        },

        mounted(){
            loginModalObj = createModalObj('loginModal');
            sendResetTokenModalObj = createModalObj('sendResetTokenModal');
            resetPasswordModalObj = createModalObj('resetPasswordModal');
            messageModalObj = createModalObj(messageModalId);
            checkAccessToken();
        }
    }).mount('#app');
}

window.addEventListener('load', setUp);

