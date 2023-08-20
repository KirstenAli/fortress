import {} from '/js/fortressNavbar.js';
import {post, get, processResponse, createModalObj, setUpMessageModal} from '/js/utils.js';

let editModalObj;
let deleteModalObj;
let lockModalObj;
let resetModalObj;
let messageModalObj;
let usersMapObj;
let messageModalId = setUpMessageModal('app');

async function handleCreateUserFormSubmit() {
    const endpoint = '/user/admin/addUser';
    const successMessage = 'User added successfully';

    const username = document.getElementById('email').value;
    const name = document.getElementById('name').value;
    const role = document.getElementById('roles').value;

    const user = {
        username: username,
        name: name,
        role: role
    };

    const response = await post(endpoint, user);
    return await processResponse(response, successMessage, messageModalObj);
}

async function findUsersStartsWith(usernamePrefix=''){

    usernamePrefix = usernamePrefix.trim() !== '' ? '/' + usernamePrefix : usernamePrefix;

    const endpoint = '/user/admin/findUsersStartsWith' +usernamePrefix;
    const response = await get(endpoint);

    const status = await processResponse(response, 'Users fetched successfully');

    if(status.success){
        const users = await response.json();
        buildUsersMap(users);
        return users;
    }

    else return null;
}

async function updateUser(user){
    const response = await post('/user/admin/update', user);
    return await processResponse(response, 'user updated successfully', messageModalObj);
}

function buildUsersMap(users){
    usersMapObj = new Map();

    users.forEach(user => {
        usersMapObj.set(user.id, user);
    });
}

async function toggleLock(user){
    let successMessage = user.accountNonLocked ? 'Account successfully locked' : 'Account successfully unlocked';

    const response = await post('/user/admin/toggleLock', user);
    const status = await processResponse(response, successMessage, messageModalObj);

    if(status.success)
        user.accountNonLocked = !user.accountNonLocked;

    return status;
}

async function emailTempPassword(user){
    const successMessage = 'A temporary password has been emailed to ' + user.username;
    const response = await post('/user/admin/emailTempPassword', user);
    return await processResponse(response, successMessage, messageModalObj);
}

async function deleteUser(user){
    const successMessage = 'User deleted successfully';
    const response = await post('/user/admin/delete', user);

    const status = await processResponse(response, successMessage, messageModalObj);

    deleteModalObj.hide();
    if(status.success){
        editModalObj.hide();
    }

    return status;
}

Vue.createApp({
    data() {
        return {
            message: '',
            users: [],
            selectedUser:{},
            userCount:0,
            usernamePrefix: '',
            authenticatedUser: {},
            repeatPassword:''
        };
    },

    methods: {
        showEditModal(id) {
            this.selectedUser = usersMapObj.get(id);
            editModalObj.show();
        },
        showLockModal(id) {
            this.selectedUser = usersMapObj.get(id);
            lockModalObj.show();
        },
        showResetModal(id) {
            this.selectedUser = usersMapObj.get(id);
            resetModalObj.show();
        },
        showDeleteModal() {
            deleteModalObj.show();
        },
        async handleCreateUserFormSubmit() {
            const status = await handleCreateUserFormSubmit();
            this.message = status.message;
            await this.getUsers();
        },

        async updateUser() {
            const status = await updateUser(this.selectedUser);
            this.message = status.message;
        },

        async toggleLock(id) {
            lockModalObj.hide();
            const selectedUser = id ? usersMapObj.get(id): this.selectedUser;
            const status = await toggleLock(selectedUser);
            this.message = status.message;
        },

        async emailTempPassword(){
            const status = await emailTempPassword(this.selectedUser);
            this.message = status.message;
            resetModalObj.hide();
        },

        async findUsersStartsWith(){
            this.users = await findUsersStartsWith(this.usernamePrefix);
            this.userCount = this.users.length;
        },

        async deleteUser(){
            const status = await deleteUser(this.selectedUser);
            this.message = status.message;
            await this.getUsers();
        }
        ,

        async getUsers(){
            this.users = await findUsersStartsWith();
            this.userCount = this.users.length;
        },
    },

    async created() {
        await this.getUsers();
    },

    mounted(){
        editModalObj = createModalObj('editModal');
        messageModalObj = createModalObj(messageModalId);
        deleteModalObj = createModalObj('deleteModal');
        lockModalObj = createModalObj('lockModal');
        resetModalObj = createModalObj('resetModal');
    }
}).mount('#app');