CUDA_VERSION=10.1

mkdir -p "lib-cuda"

"/cygdrive/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/bin/nvcc" \
-I "C:/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/include" \
-I "C:\ProgramData\NVIDIA Corporation\CUDA Samples\v$CUDA_VERSION\common\inc" \
-I "./src/cuda/include" \
-dc -odir "lib-cuda"  "src/cuda/evaluator7.cu" "src/cuda/hash.cu" 


"/cygdrive/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/bin/nvcc" \
-I "C:/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/include" \
-I "C:\ProgramData\NVIDIA Corporation\CUDA Samples\v$CUDA_VERSION\common\inc" \
-I "./src/cuda/include" \
-dlink  "lib-cuda/evaluator7.obj" "lib-cuda/hash.obj" -o "lib-cuda/link.o"


"/cygdrive/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/bin/nvcc" \
-I "C:/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v$CUDA_VERSION/include" \
-I "C:\ProgramData\NVIDIA Corporation\CUDA Samples\v$CUDA_VERSION\common\inc" \
-I "./src/cuda/include" \
-lib -o "lib-cuda/evaluator7.lib" "lib-cuda/evaluator7.obj" "lib-cuda/hash.obj" "lib-cuda/link.o"
