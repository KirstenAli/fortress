let messageModal;

function appendMessageModalTo(id){
    document.getElementById(id).appendChild(messageModal);
}

function createMessageModal(id){
    messageModal.innerHTML = `
<div class="modal fade" id="${id}" tabindex="-1" aria-labelledby="resetModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header bg-warning text-dark">
          <h4 class="modal-title" id="messageLabel">Message</h4>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p>{{ message }}</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">ok</button>
        </div>
      </div>
    </div>
  </div>
`;
}

export function setUp(messageModalId, parentId){
    messageModal = document.createElement('div');
    createMessageModal(messageModalId);
    appendMessageModalTo(parentId);
}

