   const contextPath = window.location.pathname.split('/')[1];
   
   function showLoading() {
  document.getElementById('loading').style.display = 'flex';
}

// Função para ocultar o loading
function hideLoading() {
  document.getElementById('loading').style.display = 'none';
}
  
  document.getElementById('loginForm').addEventListener('submit', function (event) {
                event.preventDefault();
                const username = document.getElementById('username').value;
                const password = document.getElementById('password').value;

                const queryParams = new URLSearchParams({
                    email: username,
                    senha: password,
                    acao: "login"
                }).toString();

        // Cria a instância do XMLHttpRequest
                const xhr = new XMLHttpRequest();

        // Configura o método e a URL
                xhr.open('POST', `/${contextPath}/Clientes?${queryParams}`, true);
                xhr.setRequestHeader('Content-Type', 'application/json');

        // Define o que fazer ao receber a resposta
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE) {
                        let resposta = JSON.parse(xhr.responseText); 
                        if (xhr.status === 200 && resposta.status === 1) { 
                            sessionStorage.setItem("usuario",resposta.usuario);
                            sessionStorage.setItem("idCliente",resposta.idCliente);
                            window.location.href = 'dashboard.html'; // Redireciona ao dashboard
                        } else if(xhr.status === 200 && resposta.status === 2){
                            window.location.href = 'dashboardAdm.html'; // Redireciona ao dashboard
                        } else {
                            console.error('Erro:', xhr.statusText);
                            document.getElementById('errorMessage').textContent = 'Credenciais inválidas';
                            document.getElementById('errorMessage').style.display = 'block';
                        }
                    }
                };
        // Envia a requisição
                xhr.send();
            });

            // Cadastro de usuário
            function registerUser() {
                const nome = document.getElementById('nome').value;
                const email = document.getElementById('email').value;
                const senha = document.getElementById('senha').value;
                const dataNascimento = document.getElementById('dataNascimento').value;

                fetch(`/${contextPath}/Clientes?acao=adicionarCliente`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({nome, email, senha, dataNascimento})
                })
                        .then(response => response.json())
                        .then(data => {
                                 Swal.fire(
                                            'Sucesso !!',
                                            'Cadastro realizado com sucesso !',
                                            'success'
                                            ); 
                            document.getElementById('registerForm').reset();
                            const btnCancelar = document.getElementById('cancelar');
                            btnCancelar.click();
                        })
                        .catch(error => {
                            console.error('Erro no cadastro:', error);
                            alert('Erro ao cadastrar cliente.');
                        });
            }