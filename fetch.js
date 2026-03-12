const http = require('http');
http.get('http://localhost:8080/admin/stationWarehouse/pending-pickups?stationId=1', (res) => {
  let data = '';
  res.on('data', chunk => data += chunk);
  res.on('end', () => console.log(data));
});
