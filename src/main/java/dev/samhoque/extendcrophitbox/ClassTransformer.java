package dev.samhoque.extendcrophitbox;

import net.minecraft.block.BlockBush;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
  Created by Sam Hoque
  Self Explanatory
 */

public class ClassTransformer implements IClassTransformer {
    public static boolean isDevelopment() {
        return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    public String getRemappedMethodName(String str) {
        if(!isDevelopment()) {
            str = str.replaceAll("getSelectedBoundingBox", "func_180646_a").replaceAll("collisionRayTrace", "func_180636_a");
        }
        return str;
    }

    public void injectExtendHitboxMethod(InsnList list) {
        String mainClass = Main.class.getName().replace('.', '/');
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, mainClass, "main", 'L' + mainClass + ';'));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, mainClass, "extendHitbox", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)V", false));
    }

    public MethodNode getSelectedBoundingBox(){
        //What's ASM?
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, getRemappedMethodName("getSelectedBoundingBox"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", null, null);

        InsnList list =  mn.instructions;
        injectExtendHitboxMethod(list);

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, BlockBush.class.getName().replace('.', '/'), getRemappedMethodName("getSelectedBoundingBox"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return mn;
    }

    public MethodNode collisionRayTrace(){
        //What's ASM?
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, getRemappedMethodName("collisionRayTrace"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;", null, null);

        InsnList list =  mn.instructions;
        injectExtendHitboxMethod(list);


        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, BlockBush.class.getName().replace('.', '/'), getRemappedMethodName("collisionRayTrace"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return mn;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }

        if(transformedName.equals("net.minecraft.block.BlockNetherWart") || transformedName.equals("net.minecraft.block.BlockCrops")) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);


            classNode.methods.add(getSelectedBoundingBox());
            classNode.methods.add(collisionRayTrace());

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        return basicClass;
    }
}
