const resetPasswordModal = document.createElement('div');
resetPasswordModal.innerHTML = `
<div class="modal fade" id="resetPasswordModal" tabindex="-1" role="dialog" aria-labelledby="resetPasswordModal" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header bg-warning text-dark d-block">
        <h4 class="modal-title" id="resetPasswordModalLabel">Reset Password</h4>
      </div>
      <div class="modal-body">
        <form id="resetPasswordForm">
          <div class="form-group">
            <label for="newPassword">New Password</label>
            <input type="password" class="form-control" required v-model="userDTO.password">
          </div>
          <div class="form-group">
            <label for="repeatPassword">Repeat Password</label>
            <input type="password" class="form-control" required v-model="repeatPassword">
          </div>
          <div class="form-group">
            <label for="resetToken">Reset Token</label>
            <input type="text" class="form-control" required v-model="userDTO.passwordResetCode">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="button" class="btn btn-warning" @click="resetPasswordWithToken">Reset Password</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
`;
document.getElementById('app').appendChild(resetPasswordModal);

export {resetPasswordModal};