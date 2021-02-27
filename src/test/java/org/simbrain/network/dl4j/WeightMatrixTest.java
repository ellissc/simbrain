package org.simbrain.network.dl4j;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.simbrain.network.core.Network;
import org.simbrain.network.groups.NeuronGroup;

import static org.junit.Assert.*;

public class WeightMatrixTest {

    @Test
    public void testMatrixOperations() {

        Network net = new Network();
        NeuronGroup ng1 = new NeuronGroup(net, 2);
        NeuronGroup ng2 = new NeuronGroup(net, 2);
        WeightMatrix wm = new WeightMatrix(net, ng1, ng2);

        // Set first entry to 4
        long start_time = System.currentTimeMillis();
        wm.getWeightMatrix().putScalar(0, 4);
        assertEquals(4, wm.getWeightMatrix().getDouble(0), 0.0);

        // Set to ((0,0);(0,0))
        start_time = System.currentTimeMillis();
        wm.setWeights(new double[]{0, 0, 0, 0});
        System.out.println(wm);
        assertEquals(0.0, wm.getWeightMatrix().sumNumber().doubleValue(), 0.0);

        // Add 1 to each entry. Should get ((1,1);(1,1))
        wm.getWeightMatrix().addi(1.0);
        System.out.println(wm);
        assertEquals(4.0, wm.getWeightMatrix().sumNumber().doubleValue(), 0.0);

        // Multiply by itself.  Should get ((2,2);(2,2))
        start_time = System.currentTimeMillis();
        wm.getWeightMatrix().mmuli(wm.getWeightMatrix());
        System.out.println(wm);
        assertEquals(8.0, wm.getWeightMatrix().sumNumber().doubleValue(), 0.0);
    }

    // @Test
    public void large_matrix_multiplication() {

        Network net = new Network();
        NeuronGroup ng1 = new NeuronGroup(net, 1000);
        NeuronGroup ng2 = new NeuronGroup(net, 1000);
        WeightMatrix wm = new WeightMatrix(net, ng1, ng2);
        wm.getWeightMatrix().addi(1.0001);

        long start_time = System.currentTimeMillis();
        // for (int i = 0; i < 1000; i++) {
        wm.getWeightMatrix().mmuli(wm.getWeightMatrix());
        // }
        long stop_time = System.currentTimeMillis();
        long difference = stop_time - start_time;
        System.out.println("Compute time for large matrix product: " + difference + " ms");
    }

    // Scratch-pad for quick nd4j testing
    //@Test
    public void testMMuli() {
        INDArray m1 = Nd4j.zeros(2, 2);
        INDArray m2 = Nd4j.ones(2, 2);
        INDArray m3 = Nd4j.ones(2, 2);
        m3.addi(1);

        System.out.println(m1);
        System.out.println(m2);
        System.out.println(m3);
        System.out.println("------");

        long start_time = System.currentTimeMillis();
        m1.mmuli(m2, m3);
        long stop_time = System.currentTimeMillis();
        long difference = stop_time - start_time;

        System.out.println("Compute Time: " + difference + " ms");
        System.out.println(m1);
        System.out.println(m2);
        System.out.println(m3);

    }
}