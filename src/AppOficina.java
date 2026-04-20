
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.Scanner;

/**
 * MIT License
 *
 * Copyright(c) 2022-25 João Caram <caram@pucminas.br>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class AppOficina {

    static final int MAX_PEDIDOS = 100;
    static Produto[] produtos;
    static int quantProdutos = 0;
    static String nomeArquivoDados = "produtos.txt";
    static IOrdenador<Produto> ordenador;
    static final Comparator<Produto> COMPARADOR_CODIGO = new ComparadorPorCodigo();
    static final Comparator<Produto> COMPARADOR_DESCRICAO = new ComparadorPorDescricao();
    static Produto[] produtosOrdenadosPorCodigo;
    static Produto[] produtosOrdenadosPorDescricao;

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
        System.out.println("1 - Por código");
        System.out.println("2 - Por descrição");
        
        return lerNumero("Digite sua opção", Integer.class);
    }

    static int exibirMenuBusca() {
        cabecalho();
        System.out.println("Procurar produto por:");
        System.out.println("1 - Código identificador");
        System.out.println("2 - Descrição");

        return lerNumero("Digite sua opção", Integer.class);
    }

    // #endregion
    static Produto[] carregarProdutos(String nomeArquivo){
        Scanner dados;
        Produto[] dadosCarregados;
        quantProdutos = 0;
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


    static void criarCopiasOrdenadas() {
        if (produtos == null || quantProdutos == 0) {
            produtosOrdenadosPorCodigo = new Produto[0];
            produtosOrdenadosPorDescricao = new Produto[0];
            return;
        }
        IOrdenador<Produto> ordenadorBase = new Mergesort<>();
        produtosOrdenadosPorCodigo = ordenadorBase.ordenar(produtos, COMPARADOR_CODIGO);
        produtosOrdenadosPorDescricao = ordenadorBase.ordenar(produtos, COMPARADOR_DESCRICAO);
    }


    static Produto localizarProduto() {
        int criterio = exibirMenuBusca();
        if (criterio == 1) {
            Integer numero = lerNumero("Digite o identificador do produto", Integer.class);
            if (numero == null) {
                return null;
            }
            return localizarProdutoPorCodigo(numero);
        }
        if (criterio == 2) {
            System.out.print("Digite a descrição do produto: ");
            String descricao = teclado.nextLine();
            return localizarProdutoPorDescricao(descricao);
        }
        return null;
    }

    static Produto localizarProdutoPorCodigo(int codigo) {
        int inicio = 0;
        int fim = quantProdutos - 1;

        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            Produto atual = produtosOrdenadosPorCodigo[meio];
            int comparacao = Integer.compare(atual.getIdProduto(), codigo);

            if (comparacao == 0) {
                return atual;
            }
            if (comparacao < 0) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return null;
    }

    static Produto localizarProdutoPorDescricao(String descricao) {
        String criterio = descricao == null ? "" : descricao.trim();
        int inicio = 0;
        int fim = quantProdutos - 1;

        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            Produto atual = produtosOrdenadosPorDescricao[meio];
            int comparacao = atual.getDescricao().compareToIgnoreCase(criterio);

            if (comparacao == 0) {
                return atual;
            }
            if (comparacao < 0) {
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

        int opcaoOrdenador = exibirMenuOrdenadores();
        ordenador = selecionarOrdenador(opcaoOrdenador);
        if (ordenador == null) {
            return;
        }

        int opcaoComparador = exibirMenuComparadores();
        Comparator<Produto> comparador = selecionarComparador(opcaoComparador);
        if (comparador == null) {
            return;
        }

        Produto[] copiaOrdenada = ordenador.ordenar(produtos, comparador);
        if (verificarSubstituicao()) {
            produtos = Arrays.copyOf(copiaOrdenada, copiaOrdenada.length);
        }
    }

    static IOrdenador<Produto> selecionarOrdenador(int opcao) {
        switch (opcao) {
            case 1:
                return new Bubblesort<>();
            case 2:
                return new InsertSort<>();
            case 3:
                return new SelectionSort<>();
            case 4:
                return new Mergesort<>();
            default:
                return null;
        }
    }

    static Comparator<Produto> selecionarComparador(int opcao) {
        switch (opcao) {
            case 1:
                return COMPARADOR_CODIGO;
            case 2:
                return COMPARADOR_DESCRICAO;
            default:
                return null;
        }
    }

    static void embaralharProdutos(){
        Collections.shuffle(Arrays.asList(produtos));
    }

    static boolean verificarSubstituicao(){
        cabecalho();
        System.out.print("Deseja sobrescrever os dados originais pelos ordenados (S/N)?");
        String resposta = teclado.nextLine().toUpperCase();
        return resposta.equals("S");
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
        if (produtos == null) {
            System.out.println("Não foi possível iniciar sem os dados de produtos.");
            teclado.close();
            return;
        }
        criarCopiasOrdenadas();
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
