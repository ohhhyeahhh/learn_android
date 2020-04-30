package com.example.wifi_gobang_ronin769.aty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wifi_gobang_ronin769.R;

public class InitAty extends Activity {
    private Button init_renjibtn;
    private Button init_lanyabtn;
//    private Button init_rankbtn;
    private Button init_renrenbtn;


    private InitButtonListener initButtonListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.init_layout);
    //设置沉浸式标题
//    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
//    {
//getWindow().addFlags(WindowManager());
//    }

    initView();



    }

    private void initView()
    {
        init_renjibtn=findViewById(R.id.init_renjibtn);
        init_lanyabtn=findViewById(R.id.init_lanyabtn);
//        init_rankbtn=findViewById(R.id.init_rankbtn);
        init_renrenbtn=findViewById(R.id.init_renrenbtn);

        initButtonListener=new InitButtonListener();

        init_renrenbtn.setOnClickListener(initButtonListener);
//        init_rankbtn.setOnClickListener(initButtonListener);
        init_lanyabtn.setOnClickListener(initButtonListener);
        init_renjibtn.setOnClickListener(initButtonListener);

    }


    private class InitButtonListener implements View.OnClickListener
    {
        Intent i;

        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.init_renjibtn:
                    AlertDialog mydialog =new AlertDialog.Builder(InitAty.this).create();
                    mydialog.show();
                    mydialog.getWindow().setContentView(R.layout.renjichoice_dialog);

                    mydialog.getWindow().
                            findViewById(R.id.renjichoice_jiandan)
                            .setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                i=new Intent(InitAty.this,RenjiGameAty.class);
                                InitAty.this.overridePendingTransition(R.anim.initactivity_open,0);
                                i.putExtra("flag",1);
                                startActivity(i);
                                }
                            }
                            );

                    mydialog.getWindow().findViewById(R.id.renjichoice_kunnan)
                            .setOnClickListener(new View.OnClickListener()
                          {

                              @Override
                              public void onClick(View v) {
                                  i=new Intent(InitAty.this,RenjiGameAty.class);
                                  InitAty.this.overridePendingTransition(R.anim.initactivity_open, 0);
                                  i.putExtra("flag",2);
                                  startActivity(i);
                              }
                          }
                    );
                    break;
                case R.id.init_renrenbtn:
                    i=new Intent(InitAty.this,RenRenGameAty.class);
                    InitAty.this.overridePendingTransition(R.anim.initactivity_open,0);
                    startActivity(i);
                    break;

                case R.id.init_lanyabtn:
                    i=new Intent(InitAty.this,BlueToothFindOthersAty.class);
                    InitAty.this.overridePendingTransition(R.anim.initactivity_open,0);
                    startActivity(i);
                    break;

            }
        }



    }


    long lastBackPressed=0;
@Override
public void onBackPressed()
{
long currentTime=System.currentTimeMillis();

if(currentTime-lastBackPressed<2000)
{
    super.onBackPressed();

}
else
{
    Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
}
lastBackPressed=currentTime;

}


}
