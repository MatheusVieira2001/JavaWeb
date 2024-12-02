const contextPath = window.location.pathname.split('/')[1];

function showLoading() {
  document.getElementById('loading').style.display = 'flex';
}

// Função para ocultar o loading
function hideLoading() {
  document.getElementById('loading').style.display = 'none';
}

let cart = []; // Carrinho de compras

// Função para adicionar cupcake ao carrinho
function addToCart(cupcakeId) {
    // Cria uma instância do XMLHttpRequest
    const xhr = new XMLHttpRequest();

    // Monta os parâmetros da URL
    const queryParams = new URLSearchParams({
        acao: "getCupcake",
        cupcakeId: cupcakeId
    }).toString();

    // Configura a requisição como GET com os parâmetros na URL
    xhr.open('GET', `/${contextPath}/cupcakes?${queryParams}`, true);

    // Define o que fazer quando a resposta for recebida
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                const cupcake = JSON.parse(xhr.responseText); // Converte a resposta em JSON

                // Verifica se o cupcake já existe no carrinho
                const existingCupcake = cart.find(item => item.cupcake.id === cupcake.id);
                if (existingCupcake) {
                    existingCupcake.quantidade += 1; // Se já estiver no carrinho, aumenta a quantidade
                } else {
                    // Caso contrário, adiciona o cupcake ao carrinho
                    cart.push({
                        cupcake: cupcake,
                        quantidade: 1,
                        precoUnitario: cupcake.preco
                    });
                }
                updateCartDisplay(); // Atualiza a visualização do carrinho
            } else {
                console.error('Erro ao adicionar cupcake ao carrinho:', xhr.statusText);
            }
        }
    };

    // Envia a requisição
    xhr.send();
}

// Atualiza o visual do carrinho 

function updateCartDisplay() {
    const cartCount = document.getElementById('cart-count');
    const cartItems = document.getElementById('cart-items');
    cartCount.textContent = cart.length;

    // Limpa o conteúdo do carrinho
    cartItems.innerHTML = '';

    // Cria a tabela
    const table = document.createElement('table');
    table.classList.add('table', 'table-bordered');

    // Cabeçalho da tabela
    const headerRow = document.createElement('tr');
    headerRow.innerHTML = `
        <th>Produto</th>
        <th>Quantidade</th>
        <th>Preço Unitário</th>
        <th>Total</th>
        <th>Ações</th>
    `;
    table.appendChild(headerRow);

    // Exibe os itens do carrinho
    cart.forEach((item, index) => {
        // Validando campos do item
        const sabor = item?.cupcake?.sabor || 'Produto não identificado';
        const quantidade = item?.quantidade || 0;
        const precoUnitario = item?.precoUnitario || 0;

        // Criação da linha
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${sabor}</td>
            <td>
                <button class="btn btn-sm btn-secondary" onclick="decreaseQuantity(${index})" style="background-color: black;">-</button>
                <span>${quantidade}</span>
                <button class="btn btn-sm btn-secondary" onclick="increaseQuantity(${index})" style="background-color: black;">+</button>
            </td>
            <td>R$${precoUnitario.toFixed(2)}</td>
            <td>R$${(precoUnitario * quantidade).toFixed(2)}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="removeFromCart(${index})" style="background-color: red;">Excluir</button>
            </td>
        `;
        table.appendChild(row);
    });

    // Exibe o valor total do carrinho
    const totalRow = document.createElement('tr');
    const totalValue = cart.reduce((sum, item) => sum + (item?.precoUnitario || 0) * (item?.quantidade || 0), 0);

    totalRow.innerHTML = `
        <td colspan="4" class="text-end"><strong>Total:</strong></td>
        <td><strong>R$${totalValue.toFixed(2)}</strong></td>
    `;
    table.appendChild(totalRow);

    // Adiciona a tabela ao conteúdo do carrinho
    cartItems.appendChild(table);
}

// Aumenta a quantidade de um item no carrinho
function increaseQuantity(index) {
    cart[index].quantidade += 1;
    updateCartDisplay();
}

// Diminui a quantidade de um item no carrinho
function decreaseQuantity(index) {
    if (cart[index].quantidade > 1) {
        cart[index].quantidade -= 1;
    } else {
        // Opcional: Remover o item se a quantidade for 1 e o botão "-" for clicado
        cart.splice(index, 1);
    }
    updateCartDisplay();
}

// Remove um item do carrinho
function removeFromCart(index) {
    cart.splice(index, 1);
    updateCartDisplay();
}

// Função para visualizar o carrinho
function viewCart() {
    new bootstrap.Modal(document.getElementById('cartModal')).show();
}

// Função de checkout
function checkout() {
    showLoading();
    const customerId = sessionStorage.getItem('idCliente'); // Obtém o ID do cliente da sessão
    const pedido = {
        cliente: 1, // Define o cliente como um objeto com o ID
        dataPedido: new Date().toISOString().split('T')[0], // Define a data do pedido como hoje
        total: cart.reduce((sum, item) => sum + item.precoUnitario * item.quantidade, 0),
        status: "Pendente",
        itensPedido: cart.map(item => ({
                cupcake: {id: item.cupcake.id}, // Define o cupcake como um objeto com o ID
                quantidade: item.quantidade,
                precoUnitario: item.precoUnitario
            }))
    };
    // Enviar os dados do pedido para o servidor
    fetch(`/${contextPath}/Pedidos?acao=adicionarPedido`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(pedido)
    })
            .then(response => {
                if (!response.ok) { 
                    throw new Error('Erro ao finalizar compra');
                } 
                return response.toString();
            })
            .then(data => {
                hideLoading()
                     Swal.fire(
                                            'Sucesso !!',
                                            'Pedido realizado.',
                                            'success'
                                            );
                document.getElementById('closeCart').click(); 
                //alert('Compra finalizada com sucesso! Número do pedido: ' + data.id);
                cart = []; // Limpar o carrinho após o pedido
                updateCartDisplay();
            })
            .catch(error => console.error('Erro ao finalizar compra:', error));
}


// Carrega os cupcakes disponíveis
document.addEventListener('DOMContentLoaded', function () {
    let usuario = sessionStorage.getItem("usuario");
    if(usuario == "undefined" || usuario ==""){
        logout();
    }
    document.getElementById("customer-name").textContent = usuario;
//cupcakes inativos não devem aparecer para o cliente
    fetch(`/${contextPath}/cupcakes?onlyActives=true`)
            .then(response => response.json())
            .then(data => {
                const productList = document.getElementById('product-list');
                productList.innerHTML = ''; // Limpa a lista antes de adicionar novos cupcakes
                data.forEach(cupcake => {
                    // Cria um card para cada cupcake
                    const cupcakeCard = document.createElement('div');
                    cupcakeCard.classList.add('product-card');
                    cupcakeCard.innerHTML = `
                                <h5>${cupcake.sabor}</h5>
                                <p>Cobertura: ${cupcake.cobertura}</p>
                                <p>Decoração: ${cupcake.decoracao}</p> 
                                <p><strong>R$ ${cupcake.preco.toFixed(2)}</strong></p>
                                <button class="btn btn-primary w-100" onclick="addToCart(${cupcake.id})">Adicionar ao Carrinho</button>
                            `;
                    productList.appendChild(cupcakeCard);
                });
            })//<img src="data:image/png;base64,${cupcake.imagemBase64}" alt="Cupcake Image">
            .catch(error => {
                console.error('Erro ao carregar cupcakes:', error);
            });
});

// Função de logout
function logout() {
    sessionStorage.clear(); // Remove o nome do usuário do sessionStorage
    window.location.href = 'login.html'; // Redireciona para a página inicial
}