  const contextPath = window.location.pathname.split('/')[1];
  
  function showLoading() {
  document.getElementById('loading').style.display = 'flex';
}

// Função para ocultar o loading
function hideLoading() {
  document.getElementById('loading').style.display = 'none';
}
            
            function loadClients() {
                fetch(`/${contextPath}/Clientes`, {method: 'POST'})
                        .then(response => response.json())
                        .then(data => {
                            const usuariosList = $('#usuariosList');
                            usuariosList.empty(); // Limpa a lista antes de adicionar novos cupcakes

                            data.forEach(usuario => {
                                const meses = {
                                    "jan.": "01", "fev.": "02", "mar.": "03", "abr.": "04", "mai.": "05", "jun.": "06",
                                    "jul.": "07", "ago.": "08", "set.": "09", "out.": "10", "nov.": "11", "dez.": "12"
                                };
                                // Extraindo o mês, dia e ano
                                let partesData = usuario.dataNascimento.split(" ");
                                let mes = meses[partesData[0]];  // Mês em número
                                let dia = partesData[1].replace(",", "");  // Dia
                                let ano = partesData[2];  // Ano 
                                let dataFormatada = `${ano}-${mes}-${dia}`;
                                
                                const row = `
                            <tr>
                                <td>${usuario.id}</td>
                                <td>${usuario.nome}</td>
                                <td>${usuario.email}</td>
                                <td>${usuario.senha}</td> 
                                <td>${dataFormatada}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm me-2" 
                                        onclick="editUsuarios(${usuario.id}, '${usuario.nome}', '${usuario.email}', '${usuario.senha}', '${dataFormatada}')">
                                        Editar
                                    </button>
                                    <button class="btn btn-danger btn-sm" 
                                        onclick="desativarUsuario(${usuario.id})">Desativar</button>
                                </td>
                            </tr>
                        `;

                                // Adiciona a linha ao usuariosList
                                usuariosList.append(row);
                            });
                        })
                        .catch(error => {
                            console.error("Erro ao carregar usuarios", error);
                        });
            }

            function editUsuarios(id, nome, email, senha, dataNascimento) {
                document.getElementById('usuarioId').value = id;
                document.getElementById('nome').value = nome;
                document.getElementById('email').value = email;
                document.getElementById('senha').value = email;
                document.getElementById('dataNascimento').value = dataNascimento;

                const modal = new bootstrap.Modal(document.getElementById('modalUsuario'));
                modal.show();
            }

            // Fun��o para carregar os cupcakes
            function loadCupcakes() {
               fetch(`/${contextPath}/cupcakes`, { method: 'POST' })
                        .then(response => response.json())
                        .then(data => {
                            const cupcakesList = $('#cupcakesList');
                            cupcakesList.empty(); // Limpa a lista antes de adicionar novos cupcakes

                            data.forEach(cupcake => {
                                let ativoStatus = cupcake.ativo === 'T' ? 'Sim' : 'Não';


                                const row = $(`
                    <tr>
                        <td>${cupcake.id}</td>
                        <td>${cupcake.sabor}</td>
                        <td>${cupcake.cobertura}</td>
                        <td>${cupcake.decoracao}</td>
                        <td>R$ ${cupcake.preco.toFixed(2)}</td>
                        <td>${cupcake.tipo}</td>
                        <td>${ativoStatus}</td>
                        <td>
                            <button class="btn btn-warning btn-sm me-2" 
                                onclick="editCupcake(${cupcake.id}, '${cupcake.sabor}', '${cupcake.cobertura}', '${cupcake.decoracao}', ${cupcake.preco}, '${cupcake.tipo}')">
                                Editar
                            </button>
                           <button class="btn btn-primary btn-sm" onclick="ativarCupcake(${cupcake.id})">Ativar</button>
                            <button class="btn btn-danger btn-sm" onclick="desativarCupcake(${cupcake.id})">Desativar</button>
                        </td>
                    </tr>
                `);

                                // Verifica se o cupcake está inativo (ativo = 'F') e altera a cor da fonte
                                if (cupcake.ativo === 'F') {
                                    row.css('color', 'red'); // Cor de fonte vermelha clara para cupcakes inativos
                                }

                                // Adiciona a linha à lista de cupcakes
                                cupcakesList.append(row);
                            });
                        })
                        .catch(error => {
                            console.error("Erro ao carregar cupcakes:", error);
                        });
            }
            // Editar Cupcake
            function editCupcake(id, sabor, cobertura, decoracao, preco, tipo) {
                document.getElementById('cupcakeId').value = id;
                document.getElementById('sabor').value = sabor;
                document.getElementById('cobertura').value = cobertura;
                document.getElementById('decoracao').value = decoracao;
                document.getElementById('preco').value = preco;
                document.getElementById('tipo').value = tipo;

                const modal = new bootstrap.Modal(document.getElementById('modalCupcake'));
                modal.show();
            }

            function salvarCupcake() {
                const id = document.getElementById('cupcakeId').value;
                const sabor = document.getElementById('sabor').value;
                const cobertura = document.getElementById('cobertura').value;
                const decoracao = document.getElementById('decoracao').value;
                const preco = parseFloat(document.getElementById('preco').value);
                const tipo = document.getElementById('tipo').value;

                const cupcake = {sabor, cobertura, decoracao, preco, tipo};
                const method = 'POST';
                const endpoint = `/${contextPath}/cupcakes?acao=salvar`;

                fetch(
                        endpoint, {
                            method,
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(cupcake)
                        }).then(() => {
                    loadCupcakes();
                    document.getElementById('formCupcake').reset();
                    bootstrap.Modal.getInstance(document.getElementById('modalCupcake')).hide();
                });
            }
 
            // Salvar Cupcake
            document.getElementById('btnSaveCupcake').addEventListener('click', () => {
                salvarCupcake();
            });
            
            document.getElementById('btnLogoff').addEventListener('click', () => {
                sessionStorage.removeItem('nmUser'); // Remove o nome do usuário do sessionStorage
                window.location.href = 'login.html'; // Redireciona para a página inicial
            }); 

            // Deletar Cupcake
            function desativarCupcake(id) {
                Swal.fire({
                    title: 'Tem certeza?',
                    text: "",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Sim, desativar!',
                    cancelButtonText: 'Cancelar',
                    reverseButtons: true
                }).then((result) => {
                    if (result.isConfirmed) { 
                        fetch(`/${contextPath}/cupcakes?id=${id}`, {method: 'DELETE'})
                                .then(() => {
                                    Swal.fire(
                                            'Desativado!',
                                            'O cupcake foi desativado com sucesso.',
                                            'success'
                                            );
                                    loadCupcakes(); // Recarregar a lista de cupcakes
                                })
                                .catch(error => {
                                    Swal.fire(
                                            'Erro!',
                                            'Ocorreu um erro ao desativar o cupcake.',
                                            'error'
                                            );
                                });
                    } else {
                        Swal.fire(
                                'Cancelado!',
                                'Ação cancelada. O cupcake não foi desativado.',
                                'info'
                                );
                    }
                });
            }

            function ativarCupcake(id) {
                Swal.fire({
                    title: 'Tem certeza?',
                    text: "",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Sim, Ativar!',
                    cancelButtonText: 'Cancelar',
                    reverseButtons: true
                }).then((result) => {
                    if (result.isConfirmed) { 
                        fetch(`/${contextPath}/cupcakes?id=${id}&ativar=true`, {method: 'DELETE'})
                                .then(() => {
                                    Swal.fire(
                                            'Ativado !',
                                            'O cupcake foi Ativado com sucesso.',
                                            'success'
                                            );
                                    loadCupcakes(); // Recarregar a lista de cupcakes
                                })
                                .catch(error => {
                                    Swal.fire(
                                            'Erro!',
                                            'Ocorreu um erro ao ativar o cupcake.',
                                            'error'
                                            );
                                });
                    } else {
                        Swal.fire(
                                'Cancelado!',
                                'Ação cancelada. O cupcake não foi ativado.',
                                'info'
                                );
                    }
                });
            }

             function loadPedidos() {
                fetch(`/${contextPath}/Pedidos`, {method: 'POST'})
                        .then(response => response.json())
                        .then(data => {
                            const pedidosList = $('#pedidosList');
                            pedidosList.empty(); // Limpa a lista antes de adicionar novos cupcakes

                            data.forEach(pedido => {
                                const meses = {
                                    "jan.": "01", "fev.": "02", "mar.": "03", "abr.": "04", "mai.": "05", "jun.": "06",
                                    "jul.": "07", "ago.": "08", "set.": "09", "out.": "10", "nov.": "11", "dez.": "12"
                                };
                                // Extraindo o mês, dia e ano
                                let partesData = pedido.dataPedido.split(" ");
                                let mes = meses[partesData[0]];  // Mês em número
                                let dia = partesData[1].replace(",", "");  // Dia
                                let ano = partesData[2];  // Ano 
                                let dataFormatada = `${ano}-${mes}-${dia}`; 
                                
                                const row = `
                            <tr>
                                <td>${pedido.id}</td>
                                <td>${pedido.nome}</td>
                                <td>${dataFormatada}</td>
                                <td>${pedido.status}</td>
                                <td>${pedido.total}</td> 
                                <td>
                                    <button class="btn btn-primary btn-sm"  >Visualizar</button>
                                    <button class="btn btn-success btn-sm"  >Confirmar</button>
                                    <button class="btn btn-warning btn-sm"  >Editar</button>
                                    <button class="btn btn-danger btn-sm"  >Cancelar</button>
                                </td>
                            </tr>
                        `;

                                // Adiciona a linha ao usuariosList
                                pedidosList.append(row);
                            });
                        })
                        .catch(error => {
                            console.error("Erro ao carregar usuarios", error);
                        });
            }


            $(document).ready(function () {
                loadCupcakes();
                loadClients();
                loadPedidos();
            });