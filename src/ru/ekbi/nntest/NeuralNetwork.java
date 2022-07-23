package ru.ekbi.nntest;

import java.util.function.UnaryOperator;

/**
 * https://tproger.ru/translations/neural-network-zoo-1/
 * На входе 2 S-нейрона (сенсора) - координаты X и Y
 * На выходе 2 нейрона - цвет точки (синий или зеленый)
 */
public class NeuralNetwork
{
    /**
     * Коэффициент скорости обучения (Learning rate) — это параметр градиентных алгоритмов обучения нейронных сетей, позволяющий управлять величиной коррекции весов на каждой итерации.
     * Выбирается в диапазоне от 0 до 1. Ноль указывать бессмысленно, поскольку в этом случае корректировка весов вообще производиться не будет.
     * https://wiki.loginom.ru/articles/learning-rate.html
     * https://habr.com/ru/post/469931/
     */
    private double learningRate;
    /**
     * Слои
     */
    private Layer[] layers;
    /**
     * Функция активации a(x) определяет выходное значение нейрона в зависимости от результата взвешенной суммы входов и порогового значения.
     * https://neerc.ifmo.ru/wiki/index.php?title=%D0%9F%D1%80%D0%B0%D0%BA%D1%82%D0%B8%D0%BA%D0%B8_%D1%80%D0%B5%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8_%D0%BD%D0%B5%D0%B9%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D1%85_%D1%81%D0%B5%D1%82%D0%B5%D0%B9#.D0.A4.D1.83.D0.BD.D0.BA.D1.86.D0.B8.D0.B8_.D0.B0.D0.BA.D1.82.D0.B8.D0.B2.D0.B0.D1.86.D0.B8.D0.B8
     * https://neerc.ifmo.ru/wiki/index.php?title=%D0%9F%D1%80%D0%BE%D0%B1%D0%BB%D0%B5%D0%BC%D1%8B_%D0%BD%D0%B5%D0%B9%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D1%85_%D1%81%D0%B5%D1%82%D0%B5%D0%B9
     * https://ru.coursera.org/lecture/neural-networks-deep-learning/derivatives-of-activation-functions-qcG1j
     */
    private UnaryOperator<Double> activation;

    /**
     * Производная от функции активации
     * https://neerc.ifmo.ru/wiki/index.php?title=%D0%9F%D1%80%D0%BE%D0%B1%D0%BB%D0%B5%D0%BC%D1%8B_%D0%BD%D0%B5%D0%B9%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D1%85_%D1%81%D0%B5%D1%82%D0%B5%D0%B9#.D0.9F.D1.80.D0.B8.D1.87.D0.B8.D0.BD.D1.8B
     */
    private UnaryOperator<Double> derivative;

    /**
     *
     * @param learningRate - Коэффициент скорости обучения (Learning rate) — позволяет управлять величиной коррекции весов на каждой итерации
     * @param activation - функция активации
     * @param derivative - производная от функции активации
     * @param sizes - количество элементов (нейронов) на каждом слое нейронной сети. Первый и последний слои должны состоять из 2-х элементов,
     *              так как входной слой - координаты (X,Y), а выходной - тип(цвет) точки
     */
    public NeuralNetwork(double learningRate, UnaryOperator<Double> activation, UnaryOperator<Double> derivative, int... sizes) {
        this.learningRate = learningRate;
        this.activation = activation;
        this.derivative = derivative;
        layers = new Layer[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            int nextSize = (i < sizes.length - 1) ? sizes[i + 1] : 0;
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0; j < sizes[i]; j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0; // не понял почему так. Кажется, они должны быть равен 1
                //layers[i].biases[j] = 1; //попробовал так, не заметил, что что-то изменилось.
                for (int k = 0; k < nextSize; k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0; //Веса каждой нейронной связи. Случайное число от -1 до +1
                }
            }
        }
    }

    /**
     * Обновляем значения слоев нейросети в соответствии с переданными на вход (первый слой нейронов) координатами точки,
     * которые преобразованы в диапазон от -0,5 до 0,5
     * ФАКТИЧЕСКИ, ЭТО И ЕСТЬ МОДЕЛЬ! ЕСЛИ ВЕСА УЖЕ ПОДОБРАНЫ ПРАВИЛЬНО (т.е. сеть обучена, само обучение проходит в backpropagation()),
     * то подавая на вход координаты, на выходе получаем тип(цвет) точки.
     *
     * https://habr.com/ru/company/wunderfund/blog/313696/
     * @param inputs массив из 2-х значений (точки X и Y), каждое из которых преобразовано в диапазон от -0,5 до +0,5
     * @return массив из 2-х чисел, представляющих собой нейроны выходного слоя, которые должны определить тип(цвет) точки по ее входным координатам
     */
    public double[] feedForward(double[] inputs) {
        //Записываем вход в первый слой (сенсоры), оба значения находится в диапазоне от -0,5 до +0,5
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1; i < layers.length; i++)  {
            Layer prevLayer = layers[i - 1];
            Layer currentLayer = layers[i];
            //для каждого нейрона текущего слоя суммируем значения нейронов предыдущего слоя с соответствующими весами связей
            //и применяем к нему ф-ю активации
            for (int j = 0; j < currentLayer.size; j++) {
                currentLayer.neurons[j] = 0;
                for (int k = 0; k < prevLayer.size; k++) {
                    currentLayer.neurons[j] += prevLayer.neurons[k] * prevLayer.weights[k][j];
                }
                currentLayer.neurons[j] += currentLayer.biases[j];
                currentLayer.neurons[j] = activation.apply(currentLayer.neurons[j]);
            }
        }
        return layers[layers.length - 1].neurons;
    }

    /**
     * Обновление весов (обучение)
     * https://ru.wikipedia.org/wiki/%D0%9C%D0%B5%D1%82%D0%BE%D0%B4_%D0%BE%D0%B1%D1%80%D0%B0%D1%82%D0%BD%D0%BE%D0%B3%D0%BE_%D1%80%D0%B0%D1%81%D0%BF%D1%80%D0%BE%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B5%D0%BD%D0%B8%D1%8F_%D0%BE%D1%88%D0%B8%D0%B1%D0%BA%D0%B8
     *
     * @param targets - желаемые(ожидаемые) значения нейронов выходного слоя (в данном случае, тип(цвет) точки). Массив из 2-х элементов, один из которых равен 0, а другой 1
     */
    public void backpropagation(double[] targets)
    {
        double[] errors = new double[layers[layers.length - 1].size]; //соответствует кол-ву нейронов выходного слоя, то есть типу точки.
        //Для каждого нейрона выходного слоя
        for (int i = 0; i < layers[layers.length - 1].size; i++)
        {
            //Величина ошибки равна разности ожидания (1 или 0) и фактического значения в выходном нейроне
            errors[i] = targets[i] - layers[layers.length - 1].neurons[i];
        }

        //Идем в обратную сторону (от предпоследнего слоя к входному)
        for (int k = layers.length - 2; k >= 0; k--) {
            Layer currentLayer = layers[k];
            Layer nextLayer = layers[k + 1];//Изначально - выходной слой

            double[] currentErrors = new double[currentLayer.size];
            double[] gradients = new double[nextLayer.size];

            //Идем по всем нейронам "следующего" слоя
            for (int i = 0; i < nextLayer.size; i++) {
                //FIXME: не ошибка, но некрасиво: errors пересоздается ниже и всегда будет иметь ту же размерность, что и nextLayer, но это не очевидно
                gradients[i] = errors[i] * derivative.apply(nextLayer.neurons[i]);
                gradients[i] *= learningRate;
            }

            double[][] deltas = new double[nextLayer.size][currentLayer.size];
            for (int i = 0; i < nextLayer.size; i++) {
                for (int j = 0; j < currentLayer.size; j++) {
                    deltas[i][j] = gradients[i] * currentLayer.neurons[j];
                }
            }

            for (int i = 0; i < currentLayer.size; i++) {
                currentErrors[i] = 0;
                for (int j = 0; j < nextLayer.size; j++) {
                    currentErrors[i] += currentLayer.weights[i][j] * errors[j];
                }
            }
            errors = new double[currentLayer.size];
            System.arraycopy(currentErrors, 0, errors, 0, currentLayer.size);
            double[][] newWeights = new double[currentLayer.weights.length][currentLayer.weights[0].length];
            for (int i = 0; i < nextLayer.size; i++) {
                for (int j = 0; j < currentLayer.size; j++) {
                    newWeights[j][i] = currentLayer.weights[j][i] + deltas[i][j];
                }
            }
            currentLayer.weights = newWeights;
            for (int i = 0; i < nextLayer.size; i++) {
                nextLayer.biases[i] += gradients[i];
            }
        }
    }
}