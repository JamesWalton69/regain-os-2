import wave
import math
import random
import os

sample_rate = 22050
duration_sec = 8  # 8 second seamless loop for each ambient soundscape
num_samples = sample_rate * duration_sec
out_dir = r"E:\Coding\Projects\regain2\app\src\main\res\raw"
os.makedirs(out_dir, exist_ok=True)

def write_wav(filename, samples):
    path = os.path.join(out_dir, filename)
    with wave.open(path, 'w') as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(sample_rate)
        # Apply gentle fade-in and fade-out at edges for seamless loop
        fade_len = int(sample_rate * 0.2)
        n = len(samples)
        for i in range(fade_len):
            gain = i / fade_len
            samples[i] *= gain
            samples[n - 1 - i] *= gain
        
        # Convert float to 16-bit PCM
        raw = bytearray()
        for s in samples:
            val = int(max(min(s, 1.0), -1.0) * 32767)
            raw.extend(val.to_bytes(2, byteorder='little', signed=True))
        wav.writeframes(raw)
    print(f"Generated {filename}: {os.path.getsize(path)} bytes")

# 1. White / Brown Noise
samples = []
last = 0.0
for i in range(num_samples):
    white = (random.random() * 2 - 1)
    brown = (last + (0.02 * white)) / 1.02
    last = brown
    samples.append(brown * 3.5)
write_wav("white_noise.wav", samples)

# 2. Rain & Thunder
samples = []
last = 0.0
for i in range(num_samples):
    white = (random.random() * 2 - 1)
    # Pinkish filter for steady rainfall
    pink = (last * 0.92) + (white * 0.08)
    last = pink
    # Occasional raindrop droplet plop
    if random.random() < 0.003:
        pink += (random.random() * 0.4)
    # Subtle thunder rumble around middle
    t = i / sample_rate
    thunder = 0.15 * math.sin(2 * math.pi * 45 * t) if (2.0 < t < 4.5) else 0.0
    samples.append((pink * 1.8) + thunder)
write_wav("rain.wav", samples)

# 3. Cozy Fireplace (Crackle & Rumble)
samples = []
for i in range(num_samples):
    t = i / sample_rate
    # Low frequency hearth resonance
    rumble = 0.08 * math.sin(2 * math.pi * 55 * t) + 0.05 * math.sin(2 * math.pi * 70 * t)
    # Crisp wood crackles and pops
    pop = 0.0
    if random.random() < 0.0015:
        pop = (random.random() * 0.8) - 0.4
    if random.random() < 0.0004:
        pop = (random.random() * 1.0) - 0.5
    hiss = (random.random() * 2 - 1) * 0.04
    samples.append(rumble + pop + hiss)
write_wav("fireplace.wav", samples)

# 4. Ocean Waves (Swells)
samples = []
for i in range(num_samples):
    t = i / sample_rate
    # Swell envelope (period ~4 seconds)
    swell = 0.5 + 0.45 * math.sin(2 * math.pi * 0.25 * t)
    noise = (random.random() * 2 - 1) * 0.25 * swell
    # Deep low tone
    rumble = 0.12 * math.sin(2 * math.pi * 40 * t) * swell
    samples.append(noise + rumble)
write_wav("ocean.wav", samples)

# 5. Forest Birds
samples = []
bird_chirps = [
    (1.2, 2200), (1.35, 2600), (1.5, 2400),
    (4.1, 1950), (4.25, 2300), (4.4, 2100), (4.55, 2500),
    (6.8, 2800), (6.95, 2600)
]
for i in range(num_samples):
    t = i / sample_rate
    # Breeze whisper
    breeze = (random.random() * 2 - 1) * 0.03
    # Bird chirps
    bird = 0.0
    for chirp_t, freq in bird_chirps:
        dt = t - chirp_t
        if 0 <= dt <= 0.09:
            env = math.sin(math.pi * (dt / 0.09))
            bird += 0.25 * math.sin(2 * math.pi * freq * dt) * env
    samples.append(breeze + bird)
write_wav("forest.wav", samples)

# 6. Lo-Fi Beats & Chords
samples = []
# Chords: Cmaj7 (C4, E4, G4, B4) -> Am7 (A3, C4, E4, G4)
# frequencies:
# C4: 261.63, E4: 329.63, G4: 392.00, B4: 493.88
# A3: 220.00, C4: 261.63, E4: 329.63, G4: 392.00
chord1 = [261.63, 329.63, 392.00, 493.88]
chord2 = [220.00, 261.63, 329.63, 392.00]
for i in range(num_samples):
    t = i / sample_rate
    chord = chord1 if t < 4.0 else chord2
    t_bar = t % 4.0
    # Soft Rhodes keyboard tone
    piano = 0.0
    env = math.exp(-t_bar * 0.7)
    for freq in chord:
        piano += 0.08 * math.sin(2 * math.pi * freq * t) * env
        # Warm 2nd harmonic
        piano += 0.03 * math.sin(2 * math.pi * (freq * 2) * t) * env
    
    # Lo-fi vinyl crackle
    crackle = 0.0
    if random.random() < 0.002:
        crackle = (random.random() * 0.15) - 0.075
    
    # Soft kick beat on beat 0 and 2.5
    kick = 0.0
    for beat_t in [0.0, 2.25]:
        dt = t_bar - beat_t
        if 0 <= dt <= 0.2:
            kick += 0.2 * math.sin(2 * math.pi * (60 - dt * 150) * dt) * (1 - dt / 0.2)
            
    samples.append(piano + crackle + kick)
write_wav("lofi.wav", samples)

print("All ambient soundscapes successfully generated!")
