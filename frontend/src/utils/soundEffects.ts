/**
 * 声音效果工具类
 * 用于播放各种交互音效
 */

// 音效类型
export enum SoundType {
  MESSAGE_SENT = 'message-sent',
  MESSAGE_RECEIVED = 'message-received',
  CONVERSATION_CREATED = 'conversation-created',
  BUTTON_CLICK = 'button-click',
  NOTIFICATION = 'notification'
}

// 音效配置
const soundConfig = {
  [SoundType.MESSAGE_SENT]: {
    frequency: 1046.5, // C6
    duration: 100,
    type: 'sine',
    volume: 0.2
  },
  [SoundType.MESSAGE_RECEIVED]: {
    frequency: 783.99, // G5
    duration: 150,
    type: 'sine',
    volume: 0.2
  },
  [SoundType.CONVERSATION_CREATED]: {
    frequency: [523.25, 659.25, 783.99], // C5, E5, G5 (C major chord)
    duration: 200,
    type: 'sine',
    volume: 0.3
  },
  [SoundType.BUTTON_CLICK]: {
    frequency: 440, // A4
    duration: 80,
    type: 'sine',
    volume: 0.1
  },
  [SoundType.NOTIFICATION]: {
    frequency: [783.99, 987.77], // G5, B5
    duration: 200,
    type: 'sine',
    volume: 0.3
  }
};

// 音频上下文
let audioContext: AudioContext | null = null;

/**
 * 初始化音频上下文
 */
const initAudioContext = (): AudioContext => {
  if (!audioContext) {
    audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
  }
  return audioContext;
};

/**
 * 播放单个音符
 * @param frequency 频率
 * @param duration 持续时间（毫秒）
 * @param type 波形类型
 * @param volume 音量（0-1）
 * @param startTime 开始时间
 */
const playTone = (
  frequency: number,
  duration: number,
  type: OscillatorType = 'sine',
  volume: number = 0.5,
  startTime: number = 0
): void => {
  try {
    const context = initAudioContext();
    const oscillator = context.createOscillator();
    const gainNode = context.createGain();
    
    oscillator.type = type as OscillatorType;
    oscillator.frequency.value = frequency;
    oscillator.connect(gainNode);
    
    gainNode.connect(context.destination);
    gainNode.gain.value = volume;
    
    // 添加淡入淡出效果
    const now = context.currentTime;
    gainNode.gain.setValueAtTime(0, now + startTime);
    gainNode.gain.linearRampToValueAtTime(volume, now + startTime + 0.01);
    gainNode.gain.linearRampToValueAtTime(0, now + startTime + duration / 1000);
    
    oscillator.start(now + startTime);
    oscillator.stop(now + startTime + duration / 1000);
  } catch (error) {
    console.error('播放音效失败:', error);
  }
};

/**
 * 播放音效
 * @param type 音效类型
 */
export const playSound = (type: SoundType): void => {
  const config = soundConfig[type];
  if (!config) return;
  
  try {
    // 用户交互后初始化音频上下文
    if (!audioContext) {
      initAudioContext();
    }
    
    // 播放单个音符或和弦
    if (Array.isArray(config.frequency)) {
      config.frequency.forEach((freq, index) => {
        playTone(freq, config.duration, config.type as OscillatorType, config.volume, index * 0.08);
      });
    } else {
      playTone(config.frequency, config.duration, config.type as OscillatorType, config.volume);
    }
  } catch (error) {
    console.error('播放音效失败:', error);
  }
};

/**
 * 播放心跳声
 * @param bpm 每分钟心跳次数
 * @param duration 持续时间（毫秒）
 */
export const playHeartbeat = (bpm: number = 70, duration: number = 1000): void => {
  const interval = 60000 / bpm; // 心跳间隔（毫秒）
  const beatCount = Math.floor(duration / interval);
  
  try {
    for (let i = 0; i < beatCount; i++) {
      // 第一次心跳声（较响）
      setTimeout(() => {
        playTone(150, 100, 'sine', 0.3);
      }, i * interval);
      
      // 第二次心跳声（较弱）
      setTimeout(() => {
        playTone(150, 80, 'sine', 0.2);
      }, i * interval + 120);
    }
  } catch (error) {
    console.error('播放心跳声失败:', error);
  }
};

/**
 * 播放微风声
 * @param duration 持续时间（毫秒）
 */
export const playBreeze = async (duration: number = 2000): Promise<void> => {
  try {
    const context = initAudioContext();
    
    // 使用现代的AudioWorkletNode替代已废弃的ScriptProcessorNode
    // 首先加载白噪音处理器模块
    try {
      await context.audioWorklet.addModule('/white-noise-processor.js');
    } catch (moduleError) {
      console.warn('AudioWorklet模块加载失败，回退到兼容模式:', moduleError);
      // 回退到ScriptProcessorNode（兼容旧浏览器）
      playBreezeCompatible(duration);
      return;
    }
    
    // 创建AudioWorkletNode
    const whiteNoiseNode = new AudioWorkletNode(context, 'white-noise-processor');
    
    // 创建增益节点用于音量控制
    const gainNode = context.createGain();
    gainNode.gain.value = 0;
    
    // 连接音频节点
    whiteNoiseNode.connect(gainNode);
    gainNode.connect(context.destination);
    
    // 淡入淡出效果
    const now = context.currentTime;
    gainNode.gain.setValueAtTime(0, now);
    gainNode.gain.linearRampToValueAtTime(0.1, now + 0.5);
    gainNode.gain.linearRampToValueAtTime(0, now + duration / 1000);
    
    // 停止白噪音
    setTimeout(() => {
      whiteNoiseNode.disconnect();
      gainNode.disconnect();
    }, duration);
  } catch (error) {
    console.error('播放微风声失败:', error);
    // 回退到兼容模式
    playBreezeCompatible(duration);
  }
};

// 兼容模式：使用ScriptProcessorNode（用于不支持AudioWorklet的浏览器）
const playBreezeCompatible = (duration: number = 2000): void => {
  try {
    const context = initAudioContext();
    
    // 使用ScriptProcessorNode作为回退方案
    // @ts-ignore - 忽略类型检查，因为这是已废弃的API
    const whiteNoise = context.createScriptProcessor(4096, 1, 1);
    
    whiteNoise.onaudioprocess = (e: AudioProcessingEvent) => {
      const output = e.outputBuffer.getChannelData(0);
      for (let i = 0; i < 4096; i++) {
        // 生成柔和的白噪音
        output[i] = (Math.random() * 2 - 1) * 0.08;
      }
    };
    
    const gainNode = context.createGain();
    gainNode.gain.value = 0;
    
    whiteNoise.connect(gainNode);
    gainNode.connect(context.destination);
    
    // 淡入淡出效果
    const now = context.currentTime;
    gainNode.gain.setValueAtTime(0, now);
    gainNode.gain.linearRampToValueAtTime(0.1, now + 0.5);
    gainNode.gain.linearRampToValueAtTime(0, now + duration / 1000);
    
    // 停止白噪音
    setTimeout(() => {
      whiteNoise.disconnect();
      gainNode.disconnect();
    }, duration);
  } catch (error) {
    console.error('兼容模式播放微风声失败:', error);
  }
};

export default {
  playSound,
  playHeartbeat,
  playBreeze,
  SoundType
};