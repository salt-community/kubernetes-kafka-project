import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },   // ramp to 50 VUs
    ],
};

export default function () {
    const res = http.get('http://localhost:8080/api/health');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'has body': (r) => r.body && r.body.length > 0,
    });

    sleep(1);
}