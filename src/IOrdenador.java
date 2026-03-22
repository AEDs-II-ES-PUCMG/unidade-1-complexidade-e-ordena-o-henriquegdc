public interface IOrdenador<T>{
    public T[] ordenar(T[] dados);
    public int getComparacoes();
    public int getMovimentacoes();
    public double getTempoOrdenacao();
}