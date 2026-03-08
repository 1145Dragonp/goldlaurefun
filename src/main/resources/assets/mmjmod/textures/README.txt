# 纹理文件说明

## 物品纹理
将你的物品纹理图片（PNG 格式）放在以下位置：
- `assets/mmjmod/textures/item/mmj.png` (16x16 或 32x32 像素) - MMJ 药水
- `assets/mmjmod/textures/item/example_item.png` (16x16 或 32x32 像素)

## 方块纹理
将你的方块纹理图片（PNG 格式）放在以下位置：
- `assets/mmjmod/textures/block/example_block.png` (16x16, 32x32, 或 64x64 像素)

## 纹理规格要求

### 物品纹理
- 格式：PNG
- 推荐尺寸：16x16, 32x32, 64x64（必须是 2 的幂次方）
- 支持透明通道

### 方块纹理
- 格式：PNG
- 推荐尺寸：16x16, 32x32, 64x64（必须是 2 的幂次方）
- 不支持透明（透明部分会渲染为黑色）

## 如何创建纹理

你可以使用以下工具：
1. **Minecraft 官方资源包** - 作为参考
2. **Blockbench** - 专业的 Minecraft 模型和纹理编辑工具
3. **Paint.NET / GIMP / Photoshop** - 图像编辑软件
4. **在线像素画编辑器** - 如 Piskel、Aseprite 等

## 示例纹理结构

```
assets/mmjmod/
├── textures/
│   ├── item/
│   │   ├── mmj.png                    ⚠️ 需要添加 PNG 纹理
│   │   └── example_item.png           ⚠️ 需要添加 PNG 纹理
│   └── block/
│       └── example_block.png          ⚠️ 需要添加 PNG 纹理
├── models/
│   ├── item/
│   │   ├── mmj.json                   ✅ 药水模型
│   │   ├── example_item.json          ✅ 食物模型
│   │   └── example_block.json         ✅ 方块模型
│   └── block/
│       └── example_block.json         ✅ 方块模型
└── lang/
    ├── zh_cn.json                     ✅ 中文语言
    └── en_us.json                     ✅ 英文语言
```
