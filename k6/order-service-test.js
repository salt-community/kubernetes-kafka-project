import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '15s', target: 100 },   // ramp to 100 VUs
    ],
};

export default function () {
    const res = http.get('http://localhost:8080/api/health');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'has body': (r) => r.body && r.body.length > 0,
    });

    sleep(0.05);
}