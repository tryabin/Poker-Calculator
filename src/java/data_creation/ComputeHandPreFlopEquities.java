package data_creation;

import data_creation.structures.HoleCardsTwoPlayers;
import data_creation.structures.OutcomeTallies;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import util.JCudaSamplesUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static jcuda.driver.JCudaDriver.*;
import static jcuda.runtime.JCuda.cudaDeviceSetCacheConfig;

public class ComputeHandPreFlopEquities {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Build the CUDA source files.
        String includeDirectory = "src/cuda/include";
        String moduleFileName = JCudaSamplesUtils.prepareDefaultCubinFile("src/cuda/compute_preflop_equities.cu", "-I", includeDirectory, "-l", "lib-cuda/evaluator7");

        // Load the module file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, moduleFileName);

        // Obtain a function pointer to the global function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "compute_preflop_equities");


        // Load all needed data onto the device.
        // Combo data
        // int comboListLength = 1 << 11;
        List<HoleCardsTwoPlayers> comboList = GeneratePreflopCombos.generateHoleCardCombos();
        // comboList = comboList.subList(0, comboListLength);

        System.out.println("Number of combos = " + comboList.size());

        CUdeviceptr comboDataPointer = DeviceDataLoad.loadComboDataOntoDevice(comboList);

        // Allocate memory on the device to store the result tallies.
        CUdeviceptr outcomeTalliesPointer = DeviceDataLoad.loadOutcomeTalliesDataOntoDevice(comboList.size());

        // Load the hash tables used for hand evaluation.
        CUdeviceptr binariesByIdPointer = DeviceDataLoad.loadBinariesByIdDataOntoDevice();
        CUdeviceptr suitBitByIdPointer = DeviceDataLoad.loadSuitBitByIdDataOntoDevice();
        CUdeviceptr flushPointer = DeviceDataLoad.loadFlushDataOntoDevice();
        CUdeviceptr noFlush7Pointer = DeviceDataLoad.loadNoFlush7DataOntoDevice();
        CUdeviceptr suitsPointer = DeviceDataLoad.loadSuitsDataOntoDevice();
        CUdeviceptr dpPointer = DeviceDataLoad.loadDpDataOntoDevice();

        // Configure the cache settings.
        cudaDeviceSetCacheConfig(CUfunc_cache.CU_FUNC_CACHE_PREFER_L1);
        // cudaDeviceSetCacheConfig(CUfunc_cache.CU_FUNC_CACHE_PREFER_SHARED);
        // cudaDeviceSetCacheConfig(CUfunc_cache.CU_FUNC_CACHE_PREFER_EQUAL);
        // cudaDeviceSetCacheConfig(CUfunc_cache.CU_FUNC_CACHE_PREFER_NONE);


        // Set up the kernel parameters.
        Pointer kernelParameters = Pointer.to(
                Pointer.to(comboDataPointer),
                Pointer.to(outcomeTalliesPointer),
                Pointer.to(new int[]{comboList.size()}),
                Pointer.to(binariesByIdPointer),
                Pointer.to(suitBitByIdPointer),
                Pointer.to(flushPointer),
                Pointer.to(noFlush7Pointer),
                Pointer.to(suitsPointer),
                Pointer.to(dpPointer)
        );


        // Call the kernel function.
        long startTime = System.nanoTime();

        int numberOfThreadsPerBlock = 512;
        int numberOfBlocks = (int)Math.ceil((double)comboList.size() / numberOfThreadsPerBlock);
        int sharedMemorySize = 0;
        cuLaunchKernel(function,
                       numberOfBlocks, 1, 1,              // Number of blocks
                       numberOfThreadsPerBlock, 1, 1,     // Number of threads per block
                       sharedMemorySize, null,            // Shared memory size and stream
                       kernelParameters, null             // Kernel- and extra parameters
        );
        cuCtxSynchronize();

        // Copy the outcome tallies from the device to the host.
        int[] outcomeTallies = new int[comboList.size()*3];
        cuMemcpyDtoH(Pointer.to(outcomeTallies), outcomeTalliesPointer, outcomeTallies.length*Sizeof.INT);

        long endTime = System.nanoTime();
        System.out.println("Kernel execution time in seconds : " + (endTime - startTime) / 1e9);


        // Create a map between each combo and the number of wins, losses, and ties for that combo.
        Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies = new HashMap<>();
        for (int i = 0; i < comboList.size(); i++) {
            int wins = outcomeTallies[i*3 + 0];
            int losses = outcomeTallies[i*3 + 1];
            int ties = outcomeTallies[i*3 + 2];

            OutcomeTallies curTallies = new OutcomeTallies(wins, losses, ties);
            HoleCardsTwoPlayers combo = comboList.get(i);
            holeCardComboTallies.put(combo, curTallies);
        }

        // Output the map to a file.
        String holeCardTalliesMapFile = "holeCardComboTallies.dat";
        ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(holeCardTalliesMapFile)));
        out.writeObject(holeCardComboTallies);
        out.flush();
        out.close();
    }
}
