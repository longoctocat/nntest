package ru.ekbi.nntest;

public class Layer
{
    /**
     *
     */
    public int size;
    public double[] neurons;
    /**
     * Нейрон смещения или bias нейрон — это третий вид нейронов, используемый в большинстве нейросетей.
     * Особенность этого типа нейронов заключается в том, что его вход и выход всегда равняются 1 и они никогда не имеют входных синапсов.
     * Нейроны смещения могут, либо присутствовать в нейронной сети по одному на слое, либо полностью отсутствовать, 50/50 быть не может
     */
    public double[] biases;
    public double[][] weights;

    /**
     *
     * @param size количество нейронов на текущем слое
     * @param nextSize количество нейронов на следующем слое
     */
    public Layer(int size, int nextSize) {
        this.size = size;
        neurons = new double[size];
        biases = new double[size];
        weights = new double[size][nextSize];
    }
}