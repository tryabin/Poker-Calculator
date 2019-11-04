package data_creation;

import data_creation.structures.HoleCardsTwoPlayers;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUdeviceptr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;

public class DeviceDataLoad {

    public static CUdeviceptr loadComboDataOntoDevice(List<HoleCardsTwoPlayers> comboList) {

//      Convert the combo list to an array.
        int[] comboListData = new int[comboList.size()*5];
        for (int i = 0; i < comboList.size(); i++) {
            HoleCardsTwoPlayers combo = comboList.get(i);
            comboListData[i*5+0] = combo.getMainPlayerCards().getCard1().getId();
            comboListData[i*5+1] = combo.getMainPlayerCards().getCard2().getId();
            comboListData[i*5+2] = combo.getOtherPlayerCards().getCard1().getId();
            comboListData[i*5+3] = combo.getOtherPlayerCards().getCard2().getId();
            comboListData[i*5+4] = combo.getUniqueId();
        }

//      Copy the combo data to the device.
        CUdeviceptr comboDataPointer = new CUdeviceptr();
        cuMemAlloc(comboDataPointer, comboListData.length*Sizeof.INT);
        cuMemcpyHtoD(comboDataPointer, Pointer.to(comboListData), comboListData.length*Sizeof.INT);

        return comboDataPointer;
    }

    public static CUdeviceptr loadOutcomeTalliesDataOntoDevice(int numCombos) {
        int[] outcomeTallies = new int[numCombos*3];
        CUdeviceptr outcomeTalliesPointer = new CUdeviceptr();
        cuMemAlloc(outcomeTalliesPointer, outcomeTallies.length*Sizeof.INT);
        cuMemcpyHtoD(outcomeTalliesPointer, Pointer.to(outcomeTallies), outcomeTallies.length*Sizeof.INT);

        return outcomeTalliesPointer;
    }




    public static CUdeviceptr loadBinariesByIdDataOntoDevice() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/resources/binaries_by_id_data.dat"));
        short[] array = (short[]) in.readObject();
        in.close();
        CUdeviceptr pointer = new CUdeviceptr();
        cuMemAlloc(pointer, array.length*Sizeof.SHORT);
        cuMemcpyHtoD(pointer, Pointer.to(array), array.length*Sizeof.SHORT);

        return pointer;
    }

    public static CUdeviceptr loadSuitBitByIdDataOntoDevice() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/resources/suitbit_by_id_data.dat"));
        short[] array = (short[]) in.readObject();
        in.close();
        CUdeviceptr pointer = new CUdeviceptr();
        cuMemAlloc(pointer, array.length*Sizeof.SHORT);
        cuMemcpyHtoD(pointer, Pointer.to(array), array.length*Sizeof.SHORT);

        return pointer;
    }

    public static CUdeviceptr loadFlushDataOntoDevice() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/resources/flush_data.dat"));
        short[] array = (short[]) in.readObject();
        in.close();
        CUdeviceptr pointer = new CUdeviceptr();
        cuMemAlloc(pointer, array.length*Sizeof.SHORT);
        cuMemcpyHtoD(pointer, Pointer.to(array), array.length*Sizeof.SHORT);

        return pointer;
    }

    public static CUdeviceptr loadNoFlush7DataOntoDevice() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/resources/noflush7_data.dat"));
        short[] array = (short[]) in.readObject();
        in.close();
        CUdeviceptr pointer = new CUdeviceptr();
        cuMemAlloc(pointer, array.length*Sizeof.SHORT);
        cuMemcpyHtoD(pointer, Pointer.to(array), array.length*Sizeof.SHORT);

        return pointer;
    }

    public static CUdeviceptr loadSuitsDataOntoDevice() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/resources/suits_data.dat"));
        byte[] array = (byte[]) in.readObject();
        in.close();
        CUdeviceptr pointer = new CUdeviceptr();
        cuMemAlloc(pointer, array.length);
        cuMemcpyHtoD(pointer, Pointer.to(array), array.length);

        return pointer;
    }

    public static CUdeviceptr loadDpDataOntoDevice() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/resources/dp_data.dat"));
        int[] array = (int[]) in.readObject();
        in.close();
        CUdeviceptr pointer = new CUdeviceptr();
        cuMemAlloc(pointer, array.length*Sizeof.INT);
        cuMemcpyHtoD(pointer, Pointer.to(array), array.length*Sizeof.INT);

        return pointer;
    }
}
