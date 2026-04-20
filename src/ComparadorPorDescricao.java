import java.util.Comparator;

public class ComparadorPorDescricao implements Comparator<Produto> {

    @Override
    public int compare(Produto o1, Produto o2) {
        return o1.getDescricao().compareToIgnoreCase(o2.getDescricao());
    }
}
