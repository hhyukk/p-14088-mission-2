package com.back;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calc {

    public static int run(String input) {
        if (input == null) throw new IllegalArgumentException("입력이 없습니다.");

        // 괄호 양쪽에 공백을 추가해서 공백을 기준으로 토큰화
        String normalized = input.replace("(", " ( ").replace(")", " ) ").trim();
        List<String> tokens = new ArrayList<>(Arrays.asList(normalized.split("\\s+")));

        // 괄호 계산
        List<String> flat = resolveParentheses(tokens);

        // 괄호 없는 사칙연산
        List<String> flatNormalized = normalizeUnary(flat);
        return evalArithmetic(flatNormalized);
    }

    // 괄호 계산
    private static List<String> resolveParentheses(List<String> tokens) {
        List<String> stack = new ArrayList<>();

        for (String tk : tokens) {
            if (!")".equals(tk)) {
                stack.add(tk);
                continue;
            }

            // ')'를 만나면 '('까지 pop
            List<String> sub = new ArrayList<>();
            while (!stack.isEmpty() && !"(".equals(stack.get(stack.size() - 1))) {
                sub.add(0, stack.remove(stack.size() - 1));
            }
            stack.remove(stack.size() - 1); // '(' 제거

            // 단항 처리 후 사칙연산
            int val = evalArithmetic(normalizeUnary(sub));
            stack.add(String.valueOf(val));
        }
        return stack;
    }

    // 단항 연산자 처리
    private static List<String> normalizeUnary(List<String> src) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            String cur = src.get(i);

            boolean isPlusMinus = "+".equals(cur) || "-".equals(cur);
            boolean atStart = (i == 0);
            boolean afterOp = (!atStart && isOperator(src.get(i - 1)));

            //식의 시작이거나, 앞 토큰이 연산자일 때 등장하는 +/−는 부호로 취급
            if (isPlusMinus && (atStart || afterOp)) {
                int signed = ("-".equals(cur) ? -1 : 1) * Integer.parseInt(src.get(i + 1));
                out.add(String.valueOf(signed));
                i++; // 다음 숫자 소비
            } else {
                out.add(cur);
            }
        }
        return out;
    }

    // 사칙연산 계산
    private static int evalArithmetic(List<String> tokens) {

        // 곱셈 계산
        List<String> t = new ArrayList<>(tokens);
        for (int i = 0; i < t.size(); i++) {
            if ("*".equals(t.get(i))) {
                int left = Integer.parseInt(t.get(i - 1));
                int right = Integer.parseInt(t.get(i + 1));
                int value = left * right;

                t.set(i - 1, String.valueOf(value));
                t.remove(i);
                t.remove(i);
                i--;
            }
        }

        // +/- 계산
        int result = Integer.parseInt(t.get(0));
        for (int i = 1; i < t.size(); i += 2) {
            String op = t.get(i);
            int num = Integer.parseInt(t.get(i + 1));
            if ("+".equals(op)) result += num;
            else if ("-".equals(op)) result -= num;
        }
        return result;
    }

    private static boolean isOperator(String s) {
        return "+".equals(s) || "-".equals(s) || "*".equals(s);
    }
}
