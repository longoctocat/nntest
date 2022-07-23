package ru.ekbi.nntest;

public class LearningProcess implements Runnable
{
    private NeuralNetwork nn;
    private Canvas canvas;
    private final int iterationCount = 10000;

    public LearningProcess(NeuralNetwork nn, Canvas canvas)
    {
        this.nn = nn;
        this.canvas = canvas;
    }

    @Override
    public void run()
    {
        while (true)
        {
            int w = canvas.getInitialWidth();
            int h = canvas.getInitialHeight();
            if(canvas.getPoints().size() > 0) {
                for (int k = 0; k < iterationCount; k++) {
                    //Берем случайную точку из имеющихся
                    Point p = canvas.getPoints().get((int) (Math.random() * canvas.getPoints().size()));
                    //Преобразуем значение ее координаты в диапазон от -0,5 до 0,5
                    double nx = (double) p.x / w - 0.5;
                    double ny = (double) p.y / h - 0.5;
                    //TODO: первый слой должен всегда состоять из 2-х нейронов (нулевой элемент последнего агумента конструтора NeuralNetwork(...))
                    //Обновляем значения слоев нейросети в соответствии с переданными на вход (первый слой нейронов) координатами точки, которые преобразованы в диапазон от -0,5 до 0,5
                    nn.feedForward(new double[]{nx, ny});
                    double[] targets = new double[2];
                    if (p.type == 0) targets[0] = 1;
                    else targets[1] = 1;
                    nn.backpropagation(targets);
                }
            }
            for (int i = 0; i < w / Canvas.SCALE; i++) {
                for (int j = 0; j < h / Canvas.SCALE; j++) {
                    double nx = (double) i / w * Canvas.SCALE - 0.5;
                    double ny = (double) j / h * Canvas.SCALE - 0.5;
                    //TODO: первый слой должен всегда состоять из 2-х нейронов (нулевой элемент последнего агумента конструтора NeuralNetwork(...))
                    double[] outputs = nn.feedForward(new double[]{nx, ny});
                    double green = Math.max(0, Math.min(1, outputs[0] - outputs[1] + 0.5));
                    double blue = 1 - green;
                    green = 0.3 + green * 0.5;
                    blue = 0.5 + blue * 0.5;
                    int color = (100 << 16) | ((int)(green * 255) << 8) | (int)(blue * 255);
                    canvas.setPredictionPointType(i, j, color);
                }
            }

            canvas.repaint();
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}