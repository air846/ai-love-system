// 白噪音音频处理器 - 使用AudioWorklet替代已废弃的ScriptProcessorNode
// AudioWorkletProcessor在音频渲染线程中运行，提供更好的性能和更低的延迟
class WhiteNoiseProcessor extends AudioWorkletProcessor {
  constructor() {
    super();
    // 初始化处理器
  }

  // 定义音频参数描述符
  static get parameterDescriptors() {
    return [
      {
        name: 'amplitude',
        defaultValue: 0.08,
        minValue: 0,
        maxValue: 1
      }
    ];
  }

  // 音频处理主函数
  // inputs: 输入音频缓冲区数组
  // outputs: 输出音频缓冲区数组
  // parameters: 音频参数对象
  process(inputs, outputs, parameters) {
    const output = outputs[0];
    const amplitude = parameters.amplitude;
    
    // 如果没有输出通道，返回true继续处理
    if (output.length === 0) {
      return true;
    }

    // 获取第一个输出通道
    const outputChannel = output[0];
    const bufferLength = outputChannel.length;
    
    // 获取振幅值（可能是单个值或数组）
    const amplitudeValue = amplitude.length === 1 ? amplitude[0] : amplitude;
    
    // 生成白噪音
    for (let i = 0; i < bufferLength; i++) {
      // 生成-1到1之间的随机数，然后乘以振幅
      const currentAmplitude = Array.isArray(amplitudeValue) ? amplitudeValue[i] : amplitudeValue;
      outputChannel[i] = (Math.random() * 2 - 1) * currentAmplitude;
    }

    // 返回true表示继续处理音频
    return true;
  }
}

// 注册处理器，名称为'white-noise-processor'
registerProcessor('white-noise-processor', WhiteNoiseProcessor);