package ru.ekbi.nntest;

import java.awt.*;
import java.util.function.UnaryOperator;

public class Main
{

    public static void main(String[] args)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth() * 0.75;
        double height = screenSize.getHeight() * 0.75;
        Canvas canvas = new Canvas((int)width, (int)height);

        /**
         *  Сигмоид часто используется в качестве функции активации,
         *  поскольку множество ее возможных значений — отрезок [0,1] — совпадает с возможными значениями вероятностной меры,
         *  что делает более удобным ее предсказание.
         *  Также график сигмоиды соответствует многим естественным процессам, показывающим рост с малых значений,
         *  который ускоряется с течением времени, и достигающим своего предела[2] (например, рост популяции)
         *  https://neerc.ifmo.ru/wiki/index.php?title=%D0%9F%D1%80%D0%BE%D0%B1%D0%BB%D0%B5%D0%BC%D1%8B_%D0%BD%D0%B5%D0%B9%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D1%85_%D1%81%D0%B5%D1%82%D0%B5%D0%B9
         */
        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));

        /**
         * Производная от сигмоида:
         * σ'(x) = σ(x)(1-σ(x))
         * https://towardsdatascience.com/derivative-of-the-sigmoid-function-536880cf918e
         */
        UnaryOperator<Double> derivative = y -> y * (1 - y);
        NeuralNetwork neuralNetwork = new NeuralNetwork(0.01, sigmoid, derivative, 2, 5, 5, 2);

        LearningProcess lp = new LearningProcess(neuralNetwork, canvas);
        new Thread(lp).start();
    }
}