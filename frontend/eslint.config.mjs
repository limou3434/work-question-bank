import {dirname} from "path";
import {fileURLToPath} from "url";
import {FlatCompat} from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
    baseDirectory: __dirname,
});

const eslintConfig = [
    ...compat.extends("next/core-web-vitals", "next/typescript"),

    // 你可以在这里覆盖或禁用一些规则
    {
        rules: {
            // 禁用 'no-explicit-any' 规则
            '@typescript-eslint/no-explicit-any': 'off',
            // 禁用 'no-unused-vars' 规则
            '@typescript-eslint/no-unused-vars': 'off',
            // 禁用 'react-hooks/exhaustive-deps' 规则
            'react-hooks/exhaustive-deps': 'off',
            // 允许使用 '@ts-ignore' 注释
            '@typescript-eslint/ban-ts-comment': 'off',
        },
    },
];

export default eslintConfig;
