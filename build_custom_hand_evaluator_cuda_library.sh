CUDA_VERSION=10.1

mkdir -p "lib-cuda"

"/cygdrive/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/bin/nvcc" \
-I "C:/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/include" \
-I "C:\ProgramData\NVIDIA Corporation\CUDA Samples\v$CUDA_VERSION\common\inc" \
-I "./src/cuda/include" \
-dc -odir "lib-cuda"  "src/cuda/get_straight_flush.cu" "src/cuda/util.cu" 


"/cygdrive/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/bin/nvcc" \
-I "C:/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/include" \
-I "C:\ProgramData\NVIDIA Corporation\CUDA Samples\v$CUDA_VERSION\common\inc" \
-I "./src/cuda/include" \
-dlink  "lib-cuda/get_straight_flush.obj" "lib-cuda/util.obj" -o "lib-cuda/link.o"


"/cygdrive/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/bin/nvcc" \
-I "C:/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/include" \
-I "C:\ProgramData\NVIDIA Corporation\CUDA Samples\v$CUDA_VERSION\common\inc" \
-I "./src/cuda/include" \
-lib -o "lib-cuda/custom_hand_evaluator.lib" "lib-cuda/get_straight_flush.obj" "lib-cuda/util.obj" "lib-cuda/link.o"
