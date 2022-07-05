package com.njust;

public class Test {
    public static void main(String[] args) {
        Stu stu=new Stu();
        Teacher teacher=new Teacher();

        showInfo(stu);
        showInfo(teacher);
    }

    static void showInfo(Behavior p){
        p.eat();
        p.work();
    }

    static void showInfo(Stu p){
        p.eat();
        p.work();
    }
    static void showInfo(Teacher p){
        p.eat();
        p.work();
    }


}

interface Behavior {
    public void eat();

    public void work();
}

class Stu implements Behavior {

    @Override
    public void eat() {
        System.out.println("学生吃食堂");
    }

    @Override
    public void work() {
        System.out.println("学生上课");
    }
}

class Teacher implements Behavior {

    @Override
    public void eat() {
        System.out.println("老师吃教工餐厅");
    }

    @Override
    public void work() {
        System.out.println("老师给学生上课");
    }
}