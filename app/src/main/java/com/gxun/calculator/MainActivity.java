package com.gxun.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_ac,btn_del,btn_mod,btn_div,btn_0,btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,
            btn_mul,btn_add ,btn_sub,btn_doc,btn_equal;
    TextView history, expression, result;

    boolean operator_flag = false; //操作符标记，防止操作符连续出现
    boolean isGetResult = false; //判断是否点击了等于号
    String operator_args = "+-*/%."; //操作符字符串，判断删除的字符是否属于操作符

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取共享属性操作的工具（文件名，操作模式）
        sp = this.getSharedPreferences("data", 0);

        //初始化按钮
        btn_0 = findViewById(R.id.num_0);
        btn_1 = findViewById(R.id.num_1);
        btn_2 = findViewById(R.id.num_2);
        btn_3 = findViewById(R.id.num_3);
        btn_4 = findViewById(R.id.num_4);
        btn_5 = findViewById(R.id.num_5);
        btn_6 = findViewById(R.id.num_6);
        btn_7 = findViewById(R.id.num_7);
        btn_8 = findViewById(R.id.num_8);
        btn_9 = findViewById(R.id.num_9);
        btn_mul = findViewById(R.id.Mul);
        btn_div = findViewById(R.id.Div);
        btn_add = findViewById(R.id.Add);
        btn_sub = findViewById(R.id.Sub);
        btn_mod = findViewById(R.id.Mod);
        btn_doc = findViewById(R.id.Doc);
        btn_del =findViewById(R.id.Del);
        btn_equal = findViewById(R.id.Equal);
        btn_ac = findViewById(R.id.AC);

        history = findViewById(R.id.history);
        expression = findViewById(R.id.expression);
        result = findViewById(R.id.result);

        //设置按钮的点击事件
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_sub.setOnClickListener(this);
        btn_mul.setOnClickListener(this);
        btn_div.setOnClickListener(this);
        btn_mod.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_ac.setOnClickListener(this);
        btn_doc.setOnClickListener(this);
        btn_equal.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.num_0: addNumber("0");break;
            case R.id.num_1: addNumber("1");break;
            case R.id.num_2: addNumber("2");break;
            case R.id.num_3: addNumber("3");break;
            case R.id.num_4: addNumber("4");break;
            case R.id.num_5: addNumber("5");break;
            case R.id.num_6: addNumber("6");break;
            case R.id.num_7: addNumber("7");break;
            case R.id.num_8: addNumber("8");break;
            case R.id.num_9: addNumber("9");break;
            case R.id.Add: addOperator("+");break;
            case R.id.Sub: addOperator("-");break;
            case R.id.Mul: addOperator("*");break;
            case R.id.Div: addOperator("/");break;
            case R.id.Mod: addOperator("%");break;
            case R.id.Del: deleteText();break;
            case R.id.AC: cleanText();break;
            case R.id.Doc: addOperator(".");break;
            case R.id.Equal:
                getResult();
                isGetResult=true;
                setTextSize();
                break;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //旋转屏幕时保存数据
        outState.putString("history", history.getText().toString());
        outState.putString("expression", expression.getText().toString());
        outState.putString("result", result.getText().toString());
        outState.putBoolean("operator", operator_flag);
        outState.putBoolean("getResult", isGetResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //旋转屏幕时获取数据
        super.onRestoreInstanceState(savedInstanceState);
        String h = savedInstanceState.getString("history");
        String e = savedInstanceState.getString("expression");
        String r = savedInstanceState.getString("result");
        history.setText(h);
        expression.setText(e);
        result.setText(r);
        operator_flag = savedInstanceState.getBoolean("operator");
        isGetResult = savedInstanceState.getBoolean("getResult");
        setTextSize();
    }

    @Override
    protected void onPause() {
        //把程序置于后台时存储数据
        super.onPause();
        String h = history.getText().toString();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("history", h); //执行方法：public abstract SharedPreferences.Editor putString (String key, String value)
        editor.putString("expression", expression.getText().toString());
        editor.putString("result", result.getText().toString());
        editor.putBoolean("operator", operator_flag);
        editor.putBoolean("getResult", isGetResult);
        editor.commit(); //提交数据
    }
    @Override
    protected void onResume() {
        //切回前台时读取数据
        super.onResume();
        history.setText(sp.getString("history", ""));
        expression.setText(sp.getString("expression",""));
        result.setText(sp.getString("result",""));
        operator_flag = sp.getBoolean("operator", false);
        isGetResult = sp.getBoolean("getResult",false);
        setTextSize();
    }

    void addOperator(String ch){
        if (isGetResult){
            String rs = result.getText().toString();
            history.append(expression.getText().toString()+rs+"\n\n");
            expression.setText(rs.substring(1,rs.length()));
            result.setText("");
            isGetResult = false;
            setTextSize();
        }
        if(expression.getText().toString().equals("")){ //在没输入数字前不能输入操作符，先自动补0
            expression.setText("0");
        }
        if (!operator_flag){ //当操作符重复，不做处理
            expression.append(ch);
            operator_flag = true;
        }
    }
    void addNumber(String ch){ //输入数字重置操作符标记
        if (isGetResult){
            history.append(expression.getText().toString()+result.getText().toString()+"\n\n");
            expression.setText("");
            result.setText("");
            isGetResult = false;
            setTextSize();
        }
        expression.append(ch);
        operator_flag = false;
        getResult();
    }
    void cleanText(){ //清除所有文本
        history.setText("");
        expression.setText ("");
        result.setText("");
        operator_flag = false;
        isGetResult = false;
        setTextSize();
    }
    void deleteText(){
        isGetResult = false;
        setTextSize();
        String s = expression.getText().toString();
        if (!s.equals("")) {
            if (operator_args.indexOf(s.charAt(s.length()-1)) != -1){
                operator_flag = false;  //当删除操作符及小数点时，重置标记状态
            }
            s = s.substring(0, s.length() - 1); //删除字符
            if((s.length() > 1) && (operator_args.indexOf(s.charAt(s.length()-1)) != -1)){
                operator_flag = true;  //删除字符串后，字符串最末一个字符是操作符，标记状态
            }
            expression.setText(s);
        }
        getResult();
    }
    public static boolean isNumber(String str) {
        //判断是否为一个可识别的数字
        Pattern pattern = Pattern.compile("^[-\\+]?[\\.\\d]*$");
        return pattern.matcher(str).matches();
    }
    void getResult(){ //计算结果
        String s = expression.getText().toString();
        if(!s.equals ("")){
            //消除多输入的符号
            if(operator_args.indexOf(s.charAt(s.length()-1)) != -1){
                s = s.substring(0, s.length()-1);
            }
            //只有一个数
            if(isNumber(s)){
                result.setText("=" + s);
                Log.e("TAG", "only one variable!");
            }else {
                //多个操作数
                try {
                    ReversePolishMultiCalc.doCalc(ReversePolishMultiCalc.doMatch(s));
                    String rs = String.valueOf(ReversePolishMultiCalc.RESULT);
                    result.setText("=" + rs); //追加计算结果
                    Log.e("TAG", "success to get the result!");
                } catch (Exception e) {
                    Log.d("TAG", "the expression is error!");
                }
            }
        }
    }
    void setTextSize(){ //设置文本字体大小
        if (isGetResult){
            expression.setTextSize(25);
            result.setTextSize(35);
        }else{
            expression.setTextSize(35);
            result.setTextSize(25);
        }
    }
}
