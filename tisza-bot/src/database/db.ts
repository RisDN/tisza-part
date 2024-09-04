import mysql from 'mysql2/promise';
import { config } from 'dotenv';

config();
const { env } = process;

const connection = async () => {
    return mysql.createConnection({
        host: env.DB_HOST,
        user: env.DB_USER,
        password: env.DB_PASS,
        database: env.DB_NAME
    });
};

const query = async (sql: string, values?: any) => {
    const conn = await connection();
    const [rows] = await conn.query(sql, values);
    return rows;
}

export default query;