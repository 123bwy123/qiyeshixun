const mysql = require('mysql2/promise');

async function checkDb() {
  const connection = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '123456',
    database: 'qiyeshixun_db'
  });

  try {
    const [inspections] = await connection.execute(
      `SELECT i.id, i.inspection_status, i.station_id, c.order_status, t.task_status 
       FROM inspection_order i 
       LEFT JOIN transfer_order tr ON i.transfer_id = tr.id 
       LEFT JOIN customer_order c ON tr.order_id = c.id 
       LEFT JOIN task_order t ON c.id = t.order_id`
    );
    console.log("=== All Inspections ===");
    console.table(inspections);
    
    const [tasks] = await connection.execute(
      `SELECT id, station_id, task_status, order_id FROM task_order`
    );
    console.log("=== All Tasks ===");
    console.table(tasks);
    
    const [orders] = await connection.execute(
      `SELECT id as order_id, order_status FROM customer_order WHERE order_status >= 3`
    );
    console.log("=== Relevant Orders ===");
    console.table(orders);
    
  } catch (err) {
    console.error(err);
  } finally {
    await connection.end();
  }
}

checkDb();
