const loginModal = document.createElement('div');
loginModal.innerHTML = `
<div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true" >
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header bg-warning text-dark">
        <h4 class="modal-title" id="loginModalLabel">Login</h4>
      </div>
      <div class="modal-body">
        <form id="loginForm">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" class="form-control" required v-model="authRequest.username">
          </div>
          <div class="form-group mb-2">
            <label for="password">Password</label>
            <input type="password" class="form-control" required v-model="authRequest.password">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showSendResetTokenModal">Forgot Password?</button>
            <button type="button" class="btn btn-warning" @click="handleLoginFormSubmit">Login</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
`;

document.getElementById('app').appendChild(loginModal);

export { loginModal};
