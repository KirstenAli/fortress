const sendResetTokenModal = document.createElement('div');
sendResetTokenModal.innerHTML = `
<div class="modal fade" id="sendResetTokenModal" tabindex="-1" role="dialog" aria-labelledby="sendResetTokenModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header bg-warning text-dark d-block">
        <h4 class="modal-title" id="sendResetTokenModalLabel">Send Password Reset Token</h4>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group">
            <label for="userToReset">Username</label>
            <input type="text" class="form-control" required v-model="userDTO.username">
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="button" class="btn btn-warning" @click="sendPasswordResetToken">Send Token</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
`;
document.getElementById('app').appendChild(sendResetTokenModal);

export { sendResetTokenModal};