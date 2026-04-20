import java.util.Comparator;

public class ComparadorPorValor implements Comparator<Pedido>{

	@Override
	public int compare(Pedido o1, Pedido o2) {
        int comparacaoValor = Double.compare(o1.valorFinal(), o2.valorFinal());
        if (comparacaoValor != 0) {
            return comparacaoValor;
        }
        return Integer.compare(o1.getIdPedido(), o2.getIdPedido());
	}
}
