<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty error}">
    <div class="position-fixed top-0 end-0 p-3" style="z-index:1100;">
        <div class="toast align-items-center text-white border-0 bg-danger show">
            <div class="d-flex">
                <div class="toast-body">${error}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${not empty message}">
    <div class="position-fixed top-0 end-0 p-3" style="z-index:1100; margin-top:70px;">
        <div class="toast align-items-center text-white border-0 bg-success show">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>
</c:if>