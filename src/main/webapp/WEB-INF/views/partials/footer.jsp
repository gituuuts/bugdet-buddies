<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Opsional: <jsp:param name="script" value="/js/transactions.js"/> --%>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<c:if test="${not empty param.script}">
  <script src="${pageContext.request.contextPath}${param.script}"></script>
</c:if>
</body>
</html>
