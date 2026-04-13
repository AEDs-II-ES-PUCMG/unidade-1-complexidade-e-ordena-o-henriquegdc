
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class AppOficina {

    static final int MAX_PEDIDOS = 100;
    static Produto[] produtos;
    static Produto[] produtosPorId;
    static Produto[] produtosPorDescricao;
    static int quantProdutos = 0;
    static String nomeArquivoDados = "produtos.txt";
    static IOrdenador<Produto> ordenador;

    // #region utilidades
    static Scanner teclado;

    

    static <T extends Number> T lerNumero(String mensagem, Class<T> classe) {
        System.out.print(mensagem + ": ");
        T valor;
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Tecle Enter para continuar.");
        teclado.nextLine();
    }

    static void cabecalho() {
        limparTela();
        System.out.println("XULAMBS COMÉRCIO DE COISINHAS v0.2\n================");
    }
    

    static int exibirMenuPrincipal() {
        cabecalho();
        System.out.println("1 - Procurar produto");
        System.out.println("2 - Filtrar produtos por preço máximo");
        System.out.println("3 - Ordenar produtos");
        System.out.println("4 - Embaralhar produtos");
        System.out.println("5 - Listar produtos");
        System.out.println("0 - Finalizar");
       
        return lerNumero("Digite sua opção", Integer.class);
    }

    static int exibirMenuOrdenadores() {
        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção");
        System.out.println("3 - Seleção");
        System.out.println("4 - Mergesort");
        System.out.println("0 - Finalizar");
       
        return lerNumero("Digite sua opção", Integer.class);
    }

    static int exibirMenuComparadores() {
        cabecalho();
        System.out.println("1 - Padrão");
        System.out.println("2 - Por código");
        
        return lerNumero("Digite sua opção", Integer.class);
    }

    static int exibirMenuPesquisa() {
        cabecalho();
        System.out.println("Pesquisar produto por:");
        System.out.println("1 - Identificador (ID)");
        System.out.println("2 - Descrição");
        
        return lerNumero("Digite sua opção", Integer.class);
    }

    // #endregion
    static Produto[] carregarProdutos(String nomeArquivo){
        Scanner dados;
        Produto[] dadosCarregados;
        try{
            dados = new Scanner(new File(nomeArquivo));
            int tamanho = Integer.parseInt(dados.nextLine());
            
            dadosCarregados = new Produto[tamanho];
            while (dados.hasNextLine()) {
                Produto novoProduto = Produto.criarDoTexto(dados.nextLine());
                dadosCarregados[quantProdutos] = novoProduto;
                quantProdutos++;
            }
            dados.close();
        }catch (FileNotFoundException fex){
            System.out.println("Arquivo não encontrado. Produtos não carregados");
            dadosCarregados = null;
        }
        return dadosCarregados;
    }

    static void criarCopiasProdutos() {
        if (produtos == null) return;
        
        // Criar cópias dos dados originais
        produtosPorId = Arrays.copyOf(produtos, quantProdutos);
        produtosPorDescricao = Arrays.copyOf(produtos, quantProdutos);
        
        // Ordenar por ID (usando ComparadorPorCodigo)
        Arrays.sort(produtosPorId, new ComparadorPorCodigo());
        
        // Ordenar por descrição (ordem padrão de Produto)
        Arrays.sort(produtosPorDescricao);
    }


    static Produto localizarProduto() {
        cabecalho();
        System.out.println("Localizando um produto");
        int tipoBusca = exibirMenuPesquisa();
        
        if (tipoBusca == 1) {
            return buscarPorId();
        } else if (tipoBusca == 2) {
            return buscarPorDescricao();
        }
        return null;
    }

    static Produto buscarPorId() {
        Integer numero = lerNumero("Digite o identificador do produto", Integer.class);
        if (numero == null) return null;
        
        int inicio = 0;
        int fim = quantProdutos - 1;
        
        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            int idMeio = produtosPorId[meio].hashCode();
            
            if (idMeio == numero) {
                return produtosPorId[meio];
            } else if (idMeio < numero) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return null;
    }

    static Produto buscarPorDescricao() {
        System.out.print("Digite a descrição do produto: ");
        String descricao = teclado.nextLine();
        
        // Usar busca binária em produtosPorDescricao
        int inicio = 0;
        int fim = quantProdutos - 1;
        
        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            int comparacao = produtosPorDescricao[meio].descricao.compareTo(descricao);
            
            if (comparacao == 0) {
                return produtosPorDescricao[meio];
            } else if (comparacao < 0) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return null;
    }

    private static void mostrarProduto(Produto produto) {
        cabecalho();
        String mensagem = "Dados inválidos";
        
        if(produto!=null){
            mensagem = String.format("Dados do produto:\n%s", produto);            
        }
        
        System.out.println(mensagem);
    }

    private static void filtrarPorPrecoMaximo(){
        cabecalho();
        System.out.println("Filtrando por valor máximo:");
        double valor = lerNumero("valor", Double.class);
        StringBuilder relatorio = new StringBuilder();
        for (int i = 0; i < quantProdutos; i++) {
            if(produtos[i].valorDeVenda() < valor)
            relatorio.append(produtos[i]+"\n");
        }
        System.out.println(relatorio.toString());
    }

    static void ordenarProdutos(){
        cabecalho();
        
        int opcao = exibirMenuOrdenadores();
        //Complete com a sua lógica
        ordenador = null;
    }

    static void embaralharProdutos(){
        Collections.shuffle(Arrays.asList(produtos));
    }

    static void verificarSubstituicao(Produto[] dadosOriginais, Produto[] copiaDados){
        cabecalho();
        System.out.print("Deseja sobrescrever os dados originais pelos ordenados (S/N)?");
        String resposta = teclado.nextLine().toUpperCase();
        if(resposta.equals("S"))
            dadosOriginais = Arrays.copyOf(copiaDados, copiaDados.length);
    }

    static void listarProdutos(){
        cabecalho();
        for (int i = 0; i < quantProdutos; i++) {
            System.out.println(produtos[i]);
        }
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in);
        
        produtos = carregarProdutos(nomeArquivoDados);
        criarCopiasProdutos();
        embaralharProdutos();

        int opcao = -1;
        
        do {
            opcao = exibirMenuPrincipal();
            switch (opcao) {
                case 1 -> mostrarProduto(localizarProduto());
                case 2 -> filtrarPorPrecoMaximo();
                case 3 -> ordenarProdutos();
                case 4 -> embaralharProdutos();
                case 5 -> listarProdutos();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        }while (opcao != 0);
        teclado.close();
    }                        
}
