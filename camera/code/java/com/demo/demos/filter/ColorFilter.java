package com.demo.demos.filter;

import com.demo.demos.R;
import com.demo.demos.utils.GLUtil;

import static android.opengl.GLES30.*;

/**
 * Created by wangyt on 2019/5/27
 */
public class ColorFilter extends BaseFilter {

    public static final String UNIFORM_COLOR_FLAG = "colorFlag";
    public static final String UNIFORM_TEXTURE_LUT = "textureLUT";

    public static int COLOR_FLAG = 0;
    public static int COLOR_FLAG_USE_LUT = 6;

    public int hColorFlag;
    public int hTextureLUT;
    private int LUTTextureId;

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        LUTTextureId = GLUtil.loadTextureFromRes(R.drawable.amatorka);
    }

    @Override
    public int initProgram() {
        return GLUtil.createAndLinkProgram(R.raw.texture_vertex_shader, R.raw.texture_color_fragtment_shader);
    }

    @Override
    public void initAttribLocations() {
        super.initAttribLocations();

        hColorFlag = glGetUniformLocation(program, UNIFORM_COLOR_FLAG);
        hTextureLUT = glGetUniformLocation(program, UNIFORM_TEXTURE_LUT);
    }

    @Override
    public void setExtend() {
        super.setExtend();
        glUniform1i(hColorFlag, COLOR_FLAG);
    }

    @Override
    public void bindTexture() {
        super.bindTexture();
        if (COLOR_FLAG == COLOR_FLAG_USE_LUT){
            glActiveTexture(GL_TEXTURE0 + 1);
            glBindTexture(GL_TEXTURE_2D, LUTTextureId);
            glUniform1i(hTextureLUT, 1);
        }
    }
}
