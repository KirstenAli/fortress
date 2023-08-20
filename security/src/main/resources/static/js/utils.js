import {setUp} from '/js/messageModal.js';

function Error(message) {
    this.error = true;
    this.message = message || 'Error performing operation';
}

function Success(message) {
    this.success = true;
    this.message = message || 'Operation Success';
}

export function createModalObj(modalId){
    return new bootstrap.Modal(document.getElementById(modalId), { backdrop: 'static', keyboard: false });
}

export async function post(endpoint, object, includeToken=true) {
    const headers = buildHeaders(includeToken);

    try {
        return await fetch(endpoint, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(object)
        });
    } catch (e){
        return new Error();
    }
}

export async function get(endpoint) {
    const token = localStorage.getItem('access_token');

    try {
        return await fetch(endpoint, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
    } catch (e){
        return new Error();
    }
}

function buildHeaders(includeToken=true){
    const headers = new Headers();
    headers.set('Content-Type', 'application/json');

    if(includeToken){
        const token = localStorage.getItem('access_token');
        headers.append('Authorization', 'Bearer ' + token);
    }

    return headers;
}

export async function processResponse(response, successMessage, modal){
    let status;

    if (response.ok) {
        status =new Success(successMessage);

    } else {
        if(response.error) {
            status = new Error();

        } else {
            response = await response.json();
            status = new Error(response.message);
        }
    }

    console.log(status.message);
    if (modal)
        modal.show();

    return status;
}

export function passwordsMatch(password, repeatPassword){
    return password===repeatPassword;
}

export function setUpMessageModal(parentId){
    const messageModalId = new Date().getTime();
    setUp(messageModalId,parentId);
    return messageModalId;
}